package top.flya.system.xxlJob.diy;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class JobLoginService {

    private final Map<String,String> loginCookie=new HashMap<>();

    @Value("${xxl.job.admin.address}")
    private String adminAddresses;

    @Value("${xxl.job.admin.username}")
    private String username;

    @Value("${xxl.job.admin.password}")
    private String password;

    public void login() {
        String url=adminAddresses+"/login";
        HttpResponse response = HttpRequest.post(url)
            .form("userName",username)
            .form("password",password)
            .execute();
        List<HttpCookie> cookies = response.getCookies();
        Optional<HttpCookie> cookieOpt = cookies.stream()
            .filter(cookie -> cookie.getName().equals("XXL_JOB_LOGIN_IDENTITY")).findFirst();
        if (!cookieOpt.isPresent())
            throw new RuntimeException("get xxl-job cookie error!");

        String value = cookieOpt.get().getValue();
        loginCookie.put("XXL_JOB_LOGIN_IDENTITY",value);
    }

    public String getCookie() {
        for (int i = 0; i < 3; i++) {
            String cookieStr = loginCookie.get("XXL_JOB_LOGIN_IDENTITY");
            if (cookieStr !=null) {
                return "XXL_JOB_LOGIN_IDENTITY="+cookieStr;
            }
            login();
        }
        throw new RuntimeException("get xxl-job cookie error!");
    }
}
