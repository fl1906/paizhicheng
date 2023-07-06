package top.flya.system.controller;


import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
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
import top.flya.system.domain.bo.PzcUserBo;
import top.flya.system.mapper.PzcUserMapper;
import top.flya.system.utils.WxUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/wx")
@Slf4j
public class WxController extends BaseController {

    @Value("${wx.appId}")
    private String appId;

    @Value("${wx.appSecret}")
    private String secret;

    @Value("${sa-token.token-prefix}")
    private String tokenPrefix;

    private final PzcUserMapper userMapper;

    private final WxUtils wxUtils;

    @PostMapping("/login") // 登录
    public R login(@RequestBody @Validated PzcUserBo loginUser) {
        String tokenValue = userLogin(loginUser);
        return (tokenValue != null) ? R.ok(tokenPrefix+" "+tokenValue) : R.fail("登录失败");
    }

    @GetMapping("/userInfo") // 获取用户信息
    public R userInfo() {
        LoginUser loginUser = LoginHelper.getLoginUser();
        Long userId = loginUser.getUserId();
        PzcUser user = userMapper.selectById(userId);
        if (user == null || StringUtils.isEmpty(user.getOpenid())|| user.getState() == 0) {
            return R.fail("用户不存在 或者已被禁用");
        }
        return R.ok(user);

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
