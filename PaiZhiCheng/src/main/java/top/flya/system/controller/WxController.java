package top.flya.system.controller;


import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.flya.common.constant.Constants;
import top.flya.common.core.controller.BaseController;
import top.flya.common.core.domain.R;
import top.flya.common.core.domain.event.LogininforEvent;
import top.flya.common.core.domain.model.XcxLoginUser;
import top.flya.common.enums.DeviceType;
import top.flya.common.helper.LoginHelper;
import top.flya.common.utils.MessageUtils;
import top.flya.common.utils.ServletUtils;
import top.flya.common.utils.spring.SpringUtils;
import top.flya.system.domain.PzcUser;
import top.flya.system.domain.bo.PzcUserBo;
import top.flya.system.mapper.PzcUserMapper;

import java.math.BigDecimal;


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

    private final PzcUserMapper userMapper;

    @PostMapping("/login") // 登录
    public R login(@RequestBody @Validated PzcUserBo loginUser) {
        String tokenValue = userLogin(loginUser);
        return (tokenValue != null) ? R.ok(tokenValue) : R.fail("登录失败");
    }

    public String userLogin(PzcUserBo pzcUserBo) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId +
            "&secret=" + secret + "&js_code=" + pzcUserBo.getCode() + "&grant_type=authorization_code";

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
            if (StringUtils.checkValNotNull(pzcUserBo.getPhone())) {
                newUser.setPhone(pzcUserBo.getPhone());
            }else {
                log.info("注册时 手机号为空");
                throw new RuntimeException("注册时 手机号为空");
            }
            newUser.setMoney(new BigDecimal(0));

            int insert = userMapper.insert(newUser);
            log.info("insertUser: " + insert);
            user = userMapper.selectOne(new QueryWrapper<PzcUser>().eq("openid", openId));
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
