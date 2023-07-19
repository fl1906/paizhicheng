package top.flya.system.config;

import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.flya.common.utils.JsonUtils;

/**
 * <p>
 * websocket服务器配置
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-18 16:42
 */
@Configuration
@Slf4j
public class ServerConfig {

    @Bean
    public SocketIOServer server(WsConfig wsConfig) {
        log.info("init wsConfig: {}", JsonUtils.toJsonString(wsConfig));
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(wsConfig.getHost());
        config.setPort(wsConfig.getPort());
        config.setTransports(Transport.WEBSOCKET,Transport.POLLING); //test
        SocketConfig socketConfig = config.getSocketConfig();
        socketConfig.setReuseAddress(true); //地址复用


        //这个listener可以用来进行身份验证
//        config.setAuthorizationListener(data -> {
//            // http://localhost:8081?token=xxxxxxx
//            // 例如果使用上面的链接进行connect，可以使用如下代码获取用户密码信息，本文不做身份验证
//            String token = data.getSingleUrlParam("token");
//            // 校验token的合法性，实际业务需要校验token是否过期等等，参考 spring-boot-demo-rbac-security 里的 JwtUtil
//            // 如果认证不通过会返回一个 Socket.EVENT_CONNECT_ERROR 事件
//            return StrUtil.isNotBlank(token);
//        });

        return new SocketIOServer(config);
    }

    /**
     * Spring 扫描自定义注解
     */
    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer server) {
        return new SpringAnnotationScanner(server);
    }
}
