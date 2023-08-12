package top.flya.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.flya.common.annotation.Log;
import top.flya.common.annotation.RepeatSubmit;
import top.flya.common.core.controller.BaseController;
import top.flya.common.core.domain.PageQuery;
import top.flya.common.core.domain.R;
import top.flya.common.core.page.TableDataInfo;
import top.flya.common.core.validate.AddGroup;
import top.flya.common.enums.BusinessType;
import top.flya.common.utils.JsonUtils;
import top.flya.common.utils.poi.ExcelUtil;
import top.flya.system.domain.bo.PzcUserCollectBo;
import top.flya.system.domain.vo.PzcUserCollectVo;
import top.flya.system.mapper.PzcActivityMapper;
import top.flya.system.service.IPzcUserCollectService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 用户收藏活动
 *
 * @author ruoyi
 * @date 2023-07-08
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/userCollect")
@Slf4j
public class PzcUserCollectController extends BaseController {

    private final IPzcUserCollectService iPzcUserCollectService;

    private final PzcActivityMapper pzcActivityMapper;


    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 查询用户收藏活动列表
     */
    @GetMapping("/list")
    public TableDataInfo<PzcUserCollectVo> list(PzcUserCollectBo bo, PageQuery pageQuery) {
        if(bo.getUserId() == null){
            bo.setUserId(getLoginUser().getUserId());
        }



        pageQuery.setOrderByColumn("create_time");
        pageQuery.setIsAsc("desc");
        return iPzcUserCollectService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出用户收藏活动列表
     */
    @SaCheckPermission("system:userCollect:export")
    @Log(title = "用户收藏活动", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(PzcUserCollectBo bo, HttpServletResponse response) {
        List<PzcUserCollectVo> list = iPzcUserCollectService.queryList(bo);
        ExcelUtil.exportExcel(list, "用户收藏活动", PzcUserCollectVo.class, response);
    }

    /**
     * 获取用户收藏活动详细信息
     *
     * @param collectId 主键
     */
    @GetMapping("/{collectId}")
    public R<PzcUserCollectVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long collectId) {
        return R.ok(iPzcUserCollectService.queryById(collectId));
    }

    @GetMapping("/status")
    public R<Void> status(@RequestParam("activityId") Long activityId) {
        String s = stringRedisTemplate.opsForValue().get("collect:" + getUserId() + ":" + activityId);
        if(s!=null)
        {
            return R.ok("1");
        }else {
            return R.ok("0");
        }
    }
    /**
     * 新增用户收藏活动 这里改为存入Redis 加快响应速度
     */
    @Log(title = "用户收藏/取消活动", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody PzcUserCollectBo bo) {
        log.info("用户收藏/取消活动 {}", JsonUtils.toJsonString(bo));
        //首先查询缓存是否存在
        String s = stringRedisTemplate.opsForValue().get("collect:" + getUserId() + ":" + bo.getActivityId());
        if(s!=null)
        {
            //取消收藏活动
            stringRedisTemplate.opsForValue().getAndDelete("collect:" + getUserId() + ":" + bo.getActivityId());
        }else {
            stringRedisTemplate.opsForValue().set("collect:" + getUserId() + ":" + bo.getActivityId(),"1");
        }
        return R.ok("1");

//        //校验活动Id是否存在
//        Long activityId = bo.getActivityId();
//        PzcActivityVo pzcActivityVo = pzcActivityMapper.selectVoById(activityId);
//        if(pzcActivityVo==null)
//        {
//            return R.fail("活动不存在");
//        }
//
//        bo.setUserId(getLoginUser().getUserId());
//        //不存在新增 存在则删除
//        List<PzcUserCollectVo> pzcUserCollectVos = iPzcUserCollectService.queryList(bo);
//        if(pzcUserCollectVos.size()>0)
//        {
//            log.info("用户取消收藏活动 ");
//            return toAjax(iPzcUserCollectService.deleteWithValidByIds(Collections.singletonList(pzcUserCollectVos.get(0).getCollectId()), true));
//        }
//
//        log.info("用户收藏活动 ");
//        return toAjax(iPzcUserCollectService.insertByBo(bo));
    }

}
