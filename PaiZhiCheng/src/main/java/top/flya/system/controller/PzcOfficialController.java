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
import top.flya.system.domain.vo.PzcOfficialVo;
import top.flya.system.domain.bo.PzcOfficialBo;
import top.flya.system.service.IPzcOfficialService;
import top.flya.common.core.page.TableDataInfo;

/**
 * 官方消息
 *
 * @author ruoyi
 * @date 2023-07-27
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/official")
public class PzcOfficialController extends BaseController {

    private final IPzcOfficialService iPzcOfficialService;

    /**
     * 查询官方消息列表
     */
    @SaCheckPermission("system:official:list")
    @GetMapping("/list")
    public TableDataInfo<PzcOfficialVo> list(PzcOfficialBo bo, PageQuery pageQuery) {
        return iPzcOfficialService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出官方消息列表
     */
    @SaCheckPermission("system:official:export")
    @Log(title = "官方消息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(PzcOfficialBo bo, HttpServletResponse response) {
        List<PzcOfficialVo> list = iPzcOfficialService.queryList(bo);
        ExcelUtil.exportExcel(list, "官方消息", PzcOfficialVo.class, response);
    }

    /**
     * 获取官方消息详细信息
     *
     * @param officialId 主键
     */
    @SaCheckPermission("system:official:query")
    @GetMapping("/{officialId}")
    public R<PzcOfficialVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long officialId) {
        return R.ok(iPzcOfficialService.queryById(officialId));
    }

    /**
     * 新增官方消息
     */
    @SaCheckPermission("system:official:add")
    @Log(title = "官方消息", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody PzcOfficialBo bo) {
        return toAjax(iPzcOfficialService.insertByBo(bo));
    }

    /**
     * 修改官方消息
     */
    @SaCheckPermission("system:official:edit")
    @Log(title = "官方消息", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody PzcOfficialBo bo) {
        return toAjax(iPzcOfficialService.updateByBo(bo));
    }

    /**
     * 删除官方消息
     *
     * @param officialIds 主键串
     */
    @SaCheckPermission("system:official:remove")
    @Log(title = "官方消息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{officialIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] officialIds) {
        return toAjax(iPzcOfficialService.deleteWithValidByIds(Arrays.asList(officialIds), true));
    }
}
