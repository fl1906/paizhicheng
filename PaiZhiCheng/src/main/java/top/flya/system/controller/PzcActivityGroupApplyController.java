package top.flya.system.controller;

import java.util.List;
import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
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
import top.flya.common.helper.LoginHelper;
import top.flya.common.utils.poi.ExcelUtil;
import top.flya.system.domain.vo.PzcActivityGroupApplyVo;
import top.flya.system.domain.bo.PzcActivityGroupApplyBo;
import top.flya.system.service.IPzcActivityGroupApplyService;
import top.flya.common.core.page.TableDataInfo;
import top.flya.system.utils.ActivityUtils;

/**
 * 活动组队申请列表
 *
 * @author ruoyi
 * @date 2023-07-10
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/activityGroupApply")
public class PzcActivityGroupApplyController extends BaseController {

    private final IPzcActivityGroupApplyService iPzcActivityGroupApplyService;

    private final ActivityUtils activityUtils;

    /**
     * 查询活动组队申请列表列表
     */
    @GetMapping("/list")
    public TableDataInfo<PzcActivityGroupApplyVo> list(PzcActivityGroupApplyBo bo, PageQuery pageQuery) {
        bo.setUserId(LoginHelper.getUserId());
        return iPzcActivityGroupApplyService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出活动组队申请列表列表
     */
    @Log(title = "活动组队申请列表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(PzcActivityGroupApplyBo bo, HttpServletResponse response) {
        List<PzcActivityGroupApplyVo> list = iPzcActivityGroupApplyService.queryList(bo);
        ExcelUtil.exportExcel(list, "活动组队申请列表", PzcActivityGroupApplyVo.class, response);
    }

    /**
     * 获取活动组队申请列表详细信息
     *
     * @param applyId 主键
     */
    @GetMapping("/{applyId}")
    public R<PzcActivityGroupApplyVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long applyId) {
        return R.ok(iPzcActivityGroupApplyService.queryById(applyId));
    }

    /**
     * 申请参与组队
     *
     *  1 做校验
     *  1.1 活动是否存在 1.2 活动是否已经开始 1.3 活动是否已经结束 1.4 活动是否已经满员
     *  2 组是否还存在
     */
    @Log(title = "活动组队申请列表", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody PzcActivityGroupApplyBo bo) {
        if(!activityUtils.allCheck(Math.toIntExact(bo.getActivityId()), bo.getGroupId()))
        {
            return R.fail("申请失败，活动不存在或者已经结束或者组不存在");
        }
        bo.setUserId(LoginHelper.getUserId());
        if(iPzcActivityGroupApplyService.queryByUserIdAndActivityId(bo.getUserId(), bo.getActivityId()))
        {
            return R.fail("申请失败，您已经申请过了");
        }
        return toAjax(iPzcActivityGroupApplyService.insertByBo(bo));
    }

    /**
     * 修改活动组队申请列表
     */
    @Log(title = "活动组队申请列表", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody PzcActivityGroupApplyBo bo) {
        bo.setUserId(LoginHelper.getUserId());
        if(!activityUtils.allCheck(Math.toIntExact(bo.getActivityId()), bo.getGroupId()))
        {
            return R.fail("修改失败，活动不存在或者已经结束或者组不存在");
        }
        if(!iPzcActivityGroupApplyService.queryByUserIdAndActivityId(bo.getUserId(), bo.getActivityId()))
        {
            return R.fail("修改失败，您还没有申请过该活动组");
        }
        return toAjax(iPzcActivityGroupApplyService.updateByBo(bo));
    }

    /**
     * 取消活动组队申请列表
     *
     * @param applyIds 主键串
     */
    @Log(title = "活动组队申请列表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{applyIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] applyIds) {
        return toAjax(iPzcActivityGroupApplyService.deleteWithValidByIds(Arrays.asList(applyIds), true));
    }


}
