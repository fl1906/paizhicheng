package top.flya.system.utils;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.flya.common.utils.DateUtils;
import top.flya.system.domain.vo.PzcActivityGroupVo;
import top.flya.system.domain.vo.PzcActivityVo;
import top.flya.system.mapper.PzcActivityGroupMapper;
import top.flya.system.mapper.PzcActivityMapper;
import top.flya.system.service.IPzcActivityGroupService;
import top.flya.system.service.IPzcActivityService;

import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class ActivityUtils {

    private final IPzcActivityService iPzcActivityService;

    private final PzcActivityMapper pzcActivityMapper;

    private final IPzcActivityGroupService iPzcActivityGroupService;

    private final PzcActivityGroupMapper pzcActivityGroupMapper;


    /**
     * 检查活动相关问题  活动是否存在 活动是否结束
     * @param activityId
     * @return
     */
    public Boolean checkActivity(Integer activityId) {
        log.info("checkActivity: activityId = {}", activityId);
        PzcActivityVo pzcActivityVo = iPzcActivityService.queryById(activityId);
        if (pzcActivityVo == null) {
            return false;
        }
        String endDate = pzcActivityVo.getEndDate();
        Date now = new Date();
        Date end = DateUtils.parseDate(endDate);
        return !now.after(end);
    }

    /**
     * 检查活动组是否存在 和当前组是否在当前活动下
     * @param groupId
     * @return
     */
    public Boolean checkGroup(Integer activityId,Long groupId) {
        log.info("checkGroup: groupId = {}", groupId);
        PzcActivityGroupVo pzcActivityGroupVo = iPzcActivityGroupService.queryById(groupId);
        if(pzcActivityGroupVo == null) {
            return false;
        }
        Long activityId1 = pzcActivityGroupVo.getActivityId();
        return activityId.equals(Math.toIntExact(activityId1));
    }


    public Boolean allCheck(Integer activityId, Long groupId) {
        return checkActivity(activityId) && checkGroup(activityId,groupId);
    }


}
