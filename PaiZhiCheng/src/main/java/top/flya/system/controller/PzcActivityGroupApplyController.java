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
import top.flya.common.utils.poi.ExcelUtil;
import top.flya.system.domain.vo.PzcActivityGroupApplyVo;
import top.flya.system.domain.bo.PzcActivityGroupApplyBo;
import top.flya.system.service.IPzcActivityGroupApplyService;
import top.flya.common.core.page.TableDataInfo;

/**
 * 活动组队申请列
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

    /**
     * 查询活动组队申请列列表
     */
    @SaCheckPermission("system:activityGroupApply:list")
    @GetMapping("/list")
    public TableDataInfo<PzcActivityGroupApplyVo> list(PzcActivityGroupApplyBo bo, PageQuery pageQuery) {
        return iPzcActivityGroupApplyService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出活动组队申请列列表
     */
    @SaCheckPermission("system:activityGroupApply:export")
    @Log(title = "活动组队申请列", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(PzcActivityGroupApplyBo bo, HttpServletResponse response) {
        List<PzcActivityGroupApplyVo> list = iPzcActivityGroupApplyService.queryList(bo);
        ExcelUtil.exportExcel(list, "活动组队申请列", PzcActivityGroupApplyVo.class, response);
    }

    /**
     * 获取活动组队申请列详细信息
     *
     * @param applyId 主键
     */
    @SaCheckPermission("system:activityGroupApply:query")
    @GetMapping("/{applyId}")
    public R<PzcActivityGroupApplyVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long applyId) {
        return R.ok(iPzcActivityGroupApplyService.queryById(applyId));
    }

    /**
     * 新增活动组队申请列
     */
    @SaCheckPermission("system:activityGroupApply:add")
    @Log(title = "活动组队申请列", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody PzcActivityGroupApplyBo bo) {
        return toAjax(iPzcActivityGroupApplyService.insertByBo(bo));
    }

    /**
     * 修改活动组队申请列
     */
    @SaCheckPermission("system:activityGroupApply:edit")
    @Log(title = "活动组队申请列", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody PzcActivityGroupApplyBo bo) {
        return toAjax(iPzcActivityGroupApplyService.updateByBo(bo));
    }

    /**
     * 删除活动组队申请列
     *
     * @param applyIds 主键串
     */
    @SaCheckPermission("system:activityGroupApply:remove")
    @Log(title = "活动组队申请列", businessType = BusinessType.DELETE)
    @DeleteMapping("/{applyIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] applyIds) {
        return toAjax(iPzcActivityGroupApplyService.deleteWithValidByIds(Arrays.asList(applyIds), true));
    }
}
