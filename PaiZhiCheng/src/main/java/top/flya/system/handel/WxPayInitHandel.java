package top.flya.system.handel;

import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.impl.client.CloseableHttpClient;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: 风离
 * @Date: 2022/06/03/23:16
 * @Description:
 */
@Aspect
@Component
@Slf4j
public class WxPayInitHandel {


    private String privateKey = "-----BEGIN PRIVATE KEY-----\n" +
            "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDJKRICZHhIi47i\n" +
            "QWwEsWjtVA2TBgtXIhrprdKn64MYZGHHAoISi3vPnUwd9RDdjRaxHr0yf13qO4zx\n" +
            "MRiky3GWYEiic769MQKmJZubk72hrB3u4/CRhaSxj5XTJfUy8v95/ILB3yravamA\n" +
            "tfan95xNyplBhq1eF4JHIJfBKHCrZ6jN1P7/ERzsR97bVZ0vS1uPQQYWZ+Hy9nC6\n" +
            "vsCe4c0EEldKDX5uNZ48KkIJnMVhXinf7Jv/NzIEZiPU+Cd6rSF/7kn33E1CkFah\n" +
            "KYAX6KutOFIJcuquyvHsDgEWbYqMhoEXm6QpE+g1yh1xEs+KPhBkbMFxHyEkSFsM\n" +
            "QiPJkYyHAgMBAAECggEANmfKPCVqNsyv91eUXGyTIWUTSsletdE+kCb4C3xx913m\n" +
            "6Akwns1kzhEP7iZCynkHQx46M3cpMlmq0+zgammvrekam/1MACVKPx96x4gcyKYh\n" +
            "bmPtw53unitkbTgd6gq4uAhoYQD3uTOErZAJRwJ8HroF2ygOqZ0YGh6hjJdgaarj\n" +
            "HG9vnRD7Q2CSOnxNXKqExbGW8Qjwj+MT2x/ecu7cCXqhvV7SSyRwGPA8HJ9WfjP1\n" +
            "+SOs502hrdZUPpyGVPUZdl5Pv/zsl+dCipqgAqyuJX6Y3rzoVH1XGANLhXDikQdQ\n" +
            "gQpFg0rCigf7HmVdNk1xbb5kn6CuNYt0Lpd2zGAcEQKBgQD73bILtaMntUZ2FCmS\n" +
            "7ZNrv/0LANQupmECoLjdelHkSPVFROzv1MAw+9rD7Gzcmk3qcUrIGGlbAzD9phDc\n" +
            "lW2Co0mnzUlVjhSOE4h1o96bEhlk0DTsNkG+mP418IpMaC6wzjVpVIOK+sNybE78\n" +
            "oC4Kd5DldMFbNdcNPIeMIhbRXwKBgQDMdlFA5UYmBs/gO/bDMr4y2bPYnVPIM1QN\n" +
            "BzU5BeU88UPCe6qMk/W4KTYoutMctNR7CVW6RAof64dbWSqrXfEAfS3PaVj4lqk+\n" +
            "QA3Q0mYrcDNFeqvupnCuuCRLcqj+mDSbz4TmA3wh1H0Rjr7GSBc4Nm70aI2ybibA\n" +
            "9r6D5g/N2QKBgQDi+l67CLz5Svct8Guq+qlxYDq1kNCnHc+tI5SWG+bzGQDYpQ31\n" +
            "8MAnJMF48XcFs5VmIyUmgEFqAM1EuUTW1V80bN0y+OEO1hUWGOpQQhaZn0z9OlmH\n" +
            "SjojfxMRHy4zP2xcb+lYfA5z0BsU4iCor93uFuSgtICQJ0wfpJ3vHsV7dwKBgB0G\n" +
            "Apy6rw2A1AtZl7q3vkYLnzr1gkod9yVuS4DPtG3FNcAqu9f+vNeqifSYKJWfmbXp\n" +
            "alDpjaJgVbOC4cq3qBlQq6sQoj+Pa3DZuNxWsYgjAjQvqK5U1BQJMaXAHfsd8gHY\n" +
            "IF5iSkGnHyXZ5HzTCPDC0VdCbDLS7g9gN0UT6FRpAoGAC8GKrdjDwMvkUtbA3aGy\n" +
            "99FnuD9oqaTLdcFo0O5xVNdqNK+yRf4yY99ky8RqfxJ/I1vPyU1hoviB2qYKphp1\n" +
            "HDtbu6gz4/kuosiRh8vWi2/kHREAyw20iRbRncBEmxLJuKKxNlbz5vF76RzlSTTg\n" +
            "A5zHu1EygULI4ytgxx5lTEQ=\n" +
            "-----END PRIVATE KEY-----\n";



    @Value("${wx.merchantId}")
    private String merchantId;

    @Value("${wx.merchantSerialNumber}")
    private String merchantSerialNumber;

    @Value("${wx.api3}")
    private String api3;

    public CloseableHttpClient setup() throws IOException {
        log.info("createOrder()方法执行前");
        CloseableHttpClient httpClient=null;

        // 加载商户私钥（privateKey：私钥字符串）
        PrivateKey merchantPrivateKey = PemUtil
                .loadPrivateKey(new ByteArrayInputStream(privateKey.getBytes("utf-8")));

        log.info(""+merchantId+"--"+merchantSerialNumber+"--"+merchantPrivateKey+"---"+api3);

        // 加载平台证书（mchId：商户号,mchSerialNo：商户证书序列号,apiV3Key：V3密钥）
        AutoUpdateCertificatesVerifier verifier = new AutoUpdateCertificatesVerifier(
                new WechatPay2Credentials(merchantId, new PrivateKeySigner(merchantSerialNumber, merchantPrivateKey)),api3.getBytes("utf-8"));


        //获取微信支付平台证书证书序列号
        BigInteger serialNumber = verifier.getValidCertificate().getSerialNumber();
        //转成16进制
        String serialnumber =  serialNumber.toString(16);
        //获取微信支付平台证书
        X509Certificate validCertificate = verifier.getValidCertificate();
        //获取微信支付平台证书公钥
        PublicKey publicKey = validCertificate.getPublicKey();
        //转成字符串
        String prikeyStr = Base64.encodeBase64String(publicKey.getEncoded());//Base64:package
        System.out.println("prikeyStr:"+prikeyStr);




        // 初始化httpClient
        httpClient = WechatPayHttpClientBuilder.create()
                .withMerchant(merchantId, merchantSerialNumber, merchantPrivateKey)
                .withValidator(new WechatPay2Validator(verifier)).build();

        return httpClient;
    }

    public void after(CloseableHttpClient httpClient) throws IOException {
        log.info("createOrder()方法执行后");
        httpClient.close();
    }

}
