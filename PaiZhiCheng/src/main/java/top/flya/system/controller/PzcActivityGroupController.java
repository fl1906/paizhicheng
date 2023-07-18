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
import top.flya.system.domain.PzcActivityGroup;
import top.flya.system.domain.vo.PzcActivityGroupVo;
import top.flya.system.domain.bo.PzcActivityGroupBo;
import top.flya.system.service.IPzcActivityGroupService;
import top.flya.common.core.page.TableDataInfo;

/**
 * 活动组队
 *
 * @author ruoyi
 * @date 2023-07-10
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/activityGroup")
public class PzcActivityGroupController extends BaseController {

    private final IPzcActivityGroupService iPzcActivityGroupService;

    /**
     * 查询活动组队列表
     */
    @GetMapping("/list")
    public TableDataInfo<PzcActivityGroupVo> list(PzcActivityGroupBo bo, PageQuery pageQuery) {
        return iPzcActivityGroupService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出活动组队列表
     */
    @SaCheckPermission("system:activityGroup:export")
    @Log(title = "活动组队", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(PzcActivityGroupBo bo, HttpServletResponse response) {
        List<PzcActivityGroupVo> list = iPzcActivityGroupService.queryList(bo);
        ExcelUtil.exportExcel(list, "活动组队", PzcActivityGroupVo.class, response);
    }

    /**
     * 获取活动组队详细信息
     *
     * @param groupId 主键
     */
    @GetMapping("/{groupId}")
    public R<PzcActivityGroupVo> getInfo(@NotNull(message = "主键不能为空")
                                         @PathVariable Long groupId) {
        return R.ok(iPzcActivityGroupService.queryById(groupId));
    }

    /**
     * 发起活动组队
     */
    @Log(title = "活动组队", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody PzcActivityGroupBo bo) {
        Long userId = LoginHelper.getUserId();
        bo.setUserId(userId);
        //校验活动是否存在
        if (!iPzcActivityGroupService.checkActivity(bo.getActivityId())) {
            return R.fail("活动不存在");
        }
        //是否已经发起过组队
        if (iPzcActivityGroupService.checkGroup(userId, bo.getActivityId())) {
            return R.fail("已经发起过组队 不可重复发起");
        }

        return toAjax(iPzcActivityGroupService.insertByBo(bo));
    }

    /**
     * 修改活动组队
     */
    @Log(title = "活动组队", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody PzcActivityGroupBo bo) {
        bo.setUserId(LoginHelper.getUserId());
        return toAjax(iPzcActivityGroupService.updateByBo(bo));
    }

    /**
     * 删除活动组队
     *
     * @param groupIds 主键串
     */
    @SaCheckPermission("system:activityGroup:remove")
    @Log(title = "活动组队", businessType = BusinessType.DELETE)
    @DeleteMapping("/{groupIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] groupIds) {
        return toAjax(iPzcActivityGroupService.deleteWithValidByIds(Arrays.asList(groupIds), true));
    }
}
