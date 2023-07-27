package top.flya.system.utils;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.flya.common.helper.LoginHelper;
import top.flya.system.domain.PzcUser;
import top.flya.system.domain.vo.PzcActivityGroupApplyVo;
import top.flya.system.mapper.PzcUserMapper;
import top.flya.system.service.IPzcActivityGroupApplyService;

import javax.annotation.Resource;

@Component
public class WxUtils {

    @Value("${wx.appId}")
    private String appId;

    @Value("${wx.appSecret}")
    private String secret;

    @Resource
    private PzcUserMapper userMapper;

    @Resource
    private IPzcActivityGroupApplyService iPzcActivityGroupApplyService;

    public PzcActivityGroupApplyVo  checkApplyConfirm(Long applyId)
    {

        //首先判断 这个applyId 的状态 以及是否存在
        PzcActivityGroupApplyVo pzcActivityGroupApplyVo = iPzcActivityGroupApplyService.queryById(applyId);
        if(pzcActivityGroupApplyVo==null)
        {
            throw new RuntimeException("申请不存在");
//            return R.fail("申请不存在");
        }
        Integer applyStatus = pzcActivityGroupApplyVo.getApplyStatus();
        if(applyStatus==-1||applyStatus==0||applyStatus==3)
        {
            throw  new RuntimeException("该订单位于【"+applyStatus(applyStatus)+"】状态，不可确认");
        }


        return pzcActivityGroupApplyVo;
    }




    public String applyStatus(Integer applyStatus)
    {
        if(applyStatus==-1)
        {
            return "已取消";
        }
        if(applyStatus==0)
        {
            return "位于申请列表中";
        }
        if(applyStatus==1)
        {
            return "申请通过待确认";
        }
        if(applyStatus==2)
        {
            return "已确认，进行中";
        }
        if(applyStatus==3)
        {
            return "已完成";
        }
        if(applyStatus==9)
        {
            return "发起方已确认";
        }
        if(applyStatus==10)
        {
            return "申请方已确认";
        }
        if(applyStatus==11)
        {
            return "发起方已打卡";
        }
        if (applyStatus==12)
        {
            return "申请方已打卡";
        }

        return null;
    }

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
