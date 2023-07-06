package top.flya.system.utils;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.flya.common.helper.LoginHelper;
import top.flya.system.domain.PzcUser;
import top.flya.system.mapper.PzcUserMapper;

import javax.annotation.Resource;

@Component
public class WxUtils {

    @Value("${wx.appId}")
    private String appId;

    @Value("${wx.appSecret}")
    private String secret;

    @Resource
    private PzcUserMapper userMapper;


    public String getAccessToken() {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId +
            "&secret=" + secret;
        String result = HttpUtil.get(url);
        return JSONUtil.parseObj(result).getStr("access_token");
    }
    public PzcUser checkUser()
    {
        Long userId = LoginHelper.getUserId();
        PzcUser user = userMapper.selectById(userId);
        if (user == null || StringUtils.isEmpty(user.getOpenid())|| user.getState() == 0) {
            throw new RuntimeException("用户不存在 或者已被禁用");
        }
        return user;
    }
}
