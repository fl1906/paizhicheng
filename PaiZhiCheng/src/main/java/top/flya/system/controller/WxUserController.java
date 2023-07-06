package top.flya.system.controller;


import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.util.DateUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.flya.common.constant.Constants;
import top.flya.common.core.controller.BaseController;
import top.flya.common.core.domain.R;
import top.flya.common.core.domain.event.LogininforEvent;
import top.flya.common.core.domain.model.LoginUser;
import top.flya.common.core.domain.model.XcxLoginUser;
import top.flya.common.enums.DeviceType;
import top.flya.common.helper.LoginHelper;
import top.flya.common.utils.MessageUtils;
import top.flya.common.utils.ServletUtils;
import top.flya.common.utils.spring.SpringUtils;
import top.flya.system.domain.PzcUser;
import top.flya.system.domain.PzcUserHistory;
import top.flya.system.domain.bo.PzcUserBo;
import top.flya.system.domain.vo.PzcUserHistoryVo;
import top.flya.system.mapper.PzcUserHistoryMapper;
import top.flya.system.mapper.PzcUserMapper;
import top.flya.system.utils.WxUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/wx/user")
@Slf4j
public class WxUserController extends BaseController {

    @Value("${wx.appId}")
    private String appId;

    @Value("${wx.appSecret}")
    private String secret;

    @Value("${sa-token.token-prefix}")
    private String tokenPrefix;

    private final PzcUserMapper userMapper;

    private final WxUtils wxUtils;

    private final PzcUserHistoryMapper userHistoryMapper;

    private final PzcUserController pzcUserController;

    @PostMapping("/login") // 登录
    public R login(@RequestBody @Validated PzcUserBo loginUser) {
        String tokenValue = userLogin(loginUser);
        return (tokenValue != null) ? R.ok(tokenPrefix+" "+tokenValue) : R.fail("登录失败");
    }

    @GetMapping("/userInfo") // 获取用户信息
    public R userInfo() {
        return R.ok(wxUtils.checkUser());
    }

    @PostMapping("/updateUserInfo") // 更新用户信息
    public R updateUserInfo(@RequestBody PzcUserBo pzcUserBo)
    {
        PzcUser user = wxUtils.checkUser();
        //获取现在时间和一年前的时间 并格式化
        String nowTime = DateUtils.format(new Date());
        String lastYearNow = LocalDateTime.of(LocalDate.now().minusYears(1), LocalDateTime.now().toLocalTime()).toString();
        log.info("nowTime is {} , lastYearNow is {}",nowTime,lastYearNow);

        if(!user.getNickname().equals(pzcUserBo.getNickname()))
        {
            //判断用户是否之前一年内是否更新过昵称
            List<PzcUserHistoryVo> pzcUserHistoryVos = userHistoryMapper.
                selectVoList(new QueryWrapper<PzcUserHistory>().eq("user_id", user.getUserId()).eq("type", 0).like("message", "%昵称%")
                    .between("create_time", lastYearNow, nowTime));
            if(pzcUserHistoryVos.size()>0)
            {
                return R.fail("一年内只能修改一次昵称");
            }else {
                //更新用户信息
                user.setNickname(pzcUserBo.getNickname());
                userMapper.updateById(user);
                //存入用户历史记录
                PzcUserHistory pzcUserHistory = new PzcUserHistory();
                pzcUserHistory.setUserId(Long.valueOf(user.getUserId()));
                pzcUserHistory.setType(Long.valueOf(0));
                pzcUserHistory.setMessage("昵称修改为"+pzcUserBo.getNickname());
                userHistoryMapper.insert(pzcUserHistory);
                return R.ok(userMapper.selectById(user.getUserId()));
            }
        }
        else {
            pzcUserBo.setMoney(user.getMoney());//余额不允许修改
            pzcUserBo.setUserId(user.getUserId());//用户id不允许修改
            pzcUserBo.setRealname(user.getRealname());//真实姓名不允许修改
            pzcUserBo.setPhone(user.getPhone());//手机号不允许修改
            pzcUserBo.setCreateTime(user.getCreateTime());//创建时间不允许修改
            pzcUserBo.setUpdateTime(user.getUpdateTime());//更新时间不允许修改
            pzcUserBo.setOpenid(user.getOpenid());//openid不允许修改

            //存入用户历史记录
            PzcUserHistory pzcUserHistory = new PzcUserHistory();
            pzcUserHistory.setUserId(Long.valueOf(user.getUserId()));
            pzcUserHistory.setType(Long.valueOf(0));
            pzcUserHistory.setMessage("更新用户其他信息");
            userHistoryMapper.insert(pzcUserHistory);
           return pzcUserController.edit(pzcUserBo);
        }

    }

    public String userLogin(PzcUserBo pzcUserBo) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId +
            "&secret=" + secret + "&js_code=" + pzcUserBo.getLoginCode() + "&grant_type=authorization_code";

        String response = HttpUtil.get(url);
        log.info("微信小程序登录 url : {}，response is {}", url, response);
        JSONObject wxUser = JSONObject.parseObject(response);
        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.checkValNull(wxUser) || wxUser.get("errcode") != null) {
            throw new RuntimeException("微信登录失败 可能是code过期了");
        }
        String openId = wxUser.get("openid").toString();
        //如果存在 就直接返回 不存在就新建用户
        PzcUser user = userMapper.selectOne(new QueryWrapper<PzcUser>().eq("openid", openId));
        if (user == null) {
            //存入用户信息
            PzcUser newUser = new PzcUser();
            newUser.setNickname(pzcUserBo.getNickname());
            newUser.setOpenid(openId);
            newUser.setAvatar(pzcUserBo.getAvatar());

            //新注册时 根据 POST https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=ACCESS_TOKEN 获取手机号
            String getPhoneUrl = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=" + wxUtils.getAccessToken();
            Map<String,String> codeMap =new HashMap<>();
            codeMap.put("code",pzcUserBo.getPhoneCode());
            String phoneResponse = HttpUtil.post(getPhoneUrl, JSONUtil.toJsonStr(codeMap));
            log.info("微信小程序获取用户手机号信息 url : {}，response is {}", getPhoneUrl, phoneResponse);
            cn.hutool.json.JSONObject phoneJson = JSONUtil.parseObj(phoneResponse);
            if(phoneJson.getInt("errcode") != 0){
                log.info("微信小程序获取用户手机号信息失败");
                throw new RuntimeException("微信小程序获取用户手机号信息失败");
            }
            newUser.setPhone(phoneJson.getJSONObject("phone_info").getStr("purePhoneNumber"));
            newUser.setSex(pzcUserBo.getSex());
            newUser.setMoney(new BigDecimal(0));

            int insert = userMapper.insert(newUser);
            log.info("insertUser: " + insert);
            user = userMapper.selectOne(new QueryWrapper<PzcUser>().eq("openid", openId));
        }

        if(user.getState()==0)
        {
            throw new RuntimeException("用户已被禁用");
        }


        // 此处可根据登录用户的数据不同 自行创建 loginUser
        XcxLoginUser loginUser = new XcxLoginUser();
        loginUser.setUserId(Long.valueOf(user.getUserId()));
        loginUser.setUsername(user.getNickname());
        loginUser.setUserType("微信小程序用户");
        loginUser.setOpenid(openId);
        // 生成token
        LoginHelper.loginByDevice(loginUser, DeviceType.XCX);
        recordLogininfor(user.getNickname(), MessageUtils.message("user.login.success"));
        return StpUtil.getTokenValue();
    }

    private void recordLogininfor(String username, String message) {
        LogininforEvent logininforEvent = new LogininforEvent();
        logininforEvent.setUsername(username);
        logininforEvent.setStatus(Constants.LOGIN_SUCCESS);
        logininforEvent.setMessage(message);
        logininforEvent.setRequest(ServletUtils.getRequest());
        SpringUtils.context().publishEvent(logininforEvent);
    }


}
