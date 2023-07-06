package top.flya.system.utils;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WxUtils {

    @Value("${wx.appId}")
    private String appId;

    @Value("${wx.appSecret}")
    private String secret;


    public String getAccessToken() {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId +
            "&secret=" + secret;
        String result = HttpUtil.get(url);
        return JSONUtil.parseObj(result).getStr("access_token");
    }
}
