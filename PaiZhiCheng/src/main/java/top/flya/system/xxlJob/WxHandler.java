package top.flya.system.xxlJob;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.flya.system.domain.PzcUser;
import top.flya.system.mapper.PzcUserMapper;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class WxHandler {

    @Resource
    private PzcUserMapper pzcUserMapper;


//    /**
//     * 1、简单任务示例（Bean模式）
//     */
//    @XxlJob("demoJobHandler")
//    public void demoJobHandler() throws Exception {
//        XxlJobHelper.log("XXL-JOB, Hello World.");
//
//        for (int i = 0; i < 5; i++) {
//            XxlJobHelper.log("beat at:" + i);
//        }
//        // default success
//    }

    /**
     * 每天的0点自动同步
     */
    @XxlJob("wxJobHandler")
    public void syncWxUserCancel() {
        XxlJobHelper.log("定时同步微信用户 的 取消活动次数 FL1906");
        log.info("定时同步微信用户 的 取消活动次数");
        List<PzcUser> pzcUsers = pzcUserMapper.selectList();
        for (PzcUser pzcUser : pzcUsers) {
            pzcUser.setExemptCancel(1);
            pzcUserMapper.updateById(pzcUser);
        }
        log.info("定时同步微信用户 的 取消活动次数 完成");
        XxlJobHelper.log("定时同步微信用户 的 取消活动次数 完成 FL1906");

    }
}
