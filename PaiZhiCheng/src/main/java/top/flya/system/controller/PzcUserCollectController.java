package top.flya.system.controller;

import java.util.Collections;
import java.util.List;
import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import top.flya.common.annotation.RepeatSubmit;
import top.flya.common.annotation.Log;
import top.flya.common.core.controller.BaseController;
import top.flya.common.core.domain.PageQuery;
import top.flya.common.core.domain.R;
import top.flya.common.core.validate.AddGroup;
import top.flya.common.core.validate.EditGroup;
import top.flya.common.enums.BusinessType;
import top.flya.common.utils.JsonUtils;
import top.flya.common.utils.poi.ExcelUtil;
import top.flya.system.domain.vo.PzcActivityVo;
import top.flya.system.domain.vo.PzcUserCollectVo;
import top.flya.system.domain.bo.PzcUserCollectBo;
import top.flya.system.mapper.PzcActivityMapper;
import top.flya.system.service.IPzcUserCollectService;
import top.flya.common.core.page.TableDataInfo;

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

    /**
     * 查询用户收藏活动列表
     */
    @GetMapping("/list")
    public TableDataInfo<PzcUserCollectVo> list(PzcUserCollectBo bo, PageQuery pageQuery) {
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

    /**
     * 新增用户收藏活动
     */
    @Log(title = "用户收藏/取消活动", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody PzcUserCollectBo bo) {
        log.info("用户收藏/取消活动 {}", JsonUtils.toJsonString(bo));

        //校验活动Id是否存在
        Long activityId = bo.getActivityId();
        PzcActivityVo pzcActivityVo = pzcActivityMapper.selectVoById(activityId);
        if(pzcActivityVo==null)
        {
            return R.fail("活动不存在");
        }

        bo.setUserId(getLoginUser().getUserId());
        //不存在新增 存在则删除
        List<PzcUserCollectVo> pzcUserCollectVos = iPzcUserCollectService.queryList(bo);
        if(pzcUserCollectVos.size()>0)
        {
            return toAjax(iPzcUserCollectService.deleteWithValidByIds(Collections.singletonList(pzcUserCollectVos.get(0).getCollectId()), true));
        }

        return toAjax(iPzcUserCollectService.insertByBo(bo));
    }

}
