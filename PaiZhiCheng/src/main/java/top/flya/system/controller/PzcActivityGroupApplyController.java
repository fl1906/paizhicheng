package top.flya.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.flya.common.annotation.Log;
import top.flya.common.annotation.RepeatSubmit;
import top.flya.common.core.controller.BaseController;
import top.flya.common.core.domain.PageQuery;
import top.flya.common.core.domain.R;
import top.flya.common.core.page.TableDataInfo;
import top.flya.common.core.validate.AddGroup;
import top.flya.common.core.validate.EditGroup;
import top.flya.common.enums.BusinessType;
import top.flya.common.helper.LoginHelper;
import top.flya.common.utils.poi.ExcelUtil;
import top.flya.system.domain.PzcActivityGroup;
import top.flya.system.domain.PzcActivityGroupApply;
import top.flya.system.domain.PzcUser;
import top.flya.system.domain.bo.PzcActivityGroupApplyBo;
import top.flya.system.domain.vo.PzcActivityGroupApplyVo;
import top.flya.system.mapper.PzcActivityGroupApplyMapper;
import top.flya.system.mapper.PzcActivityGroupMapper;
import top.flya.system.mapper.PzcUserMapper;
import top.flya.system.service.IPzcActivityGroupApplyService;
import top.flya.system.utils.ActivityUtils;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    private final PzcActivityGroupApplyMapper pzcActivityGroupApplyMapper;

    private final PzcUserMapper pzcUserMapper;

    private final PzcActivityGroupMapper pzcActivityGroupMapper;

    /**
     * -1 已取消
     * 0 位于申请列表中
     * 1 申请通过待确认时
     * 2 确认通过进行中
     * 3 组队结束
     * 9发起方已确认
     * 10申请方已确认
     * 11 发起方已打卡
     * 12 申请方已打卡
     * 13 发起方已评价
     * 14 申请方已评价
     * 15 双方已评价
     *
     * @return
     */
    @GetMapping("/myTrips") //我的行程
    public R<List<PzcActivityGroupApply>> myTrips() {
        //我申请 并处于进行中的活动
        Long userId = LoginHelper.getUserId();
        List<PzcActivityGroupApply> step1 = pzcActivityGroupApplyMapper.selectList(
            new QueryWrapper<PzcActivityGroupApply>()
                .eq("user_id", userId).in("apply_status", 1, 2, 9, 10, 11, 12, 13, 14));
        step1.forEach(
            p -> {
                PzcActivityGroup pzcActivityGroup = pzcActivityGroupMapper.selectById(p.getGroupId());
                PzcUser my = pzcUserMapper.selectById(p.getUserId());
                PzcUser other = pzcUserMapper.selectById(pzcActivityGroup.getUserId());
                p.setOtherMoney(pzcActivityGroup.getMoney());
                p.setOtherName(other.getNickname());
                p.setOtherAvatar(other.getAvatar());
                p.setOtherUserId(String.valueOf(other.getUserId()));
                p.setOtherLevel(Math.toIntExact(other.getUserLevel()));
                p.setMyAvatar(my.getAvatar());
                p.setTitle(pzcActivityGroup.getTitle());
            }
        );

        //申请我的 并处于进行中的活动
        //1 找出所有我创建的组
        List<PzcActivityGroup> pzcActivityGroups = pzcActivityGroupMapper.selectList(new QueryWrapper<PzcActivityGroup>().eq("user_id", userId));
        List<Long> groupIds = pzcActivityGroups.stream().map(PzcActivityGroup::getGroupId).collect(java.util.stream.Collectors.toList());
        List<PzcActivityGroupApply> step2 = pzcActivityGroupApplyMapper.selectList(new QueryWrapper<>(new PzcActivityGroupApply()).in("group_id", groupIds).in("apply_status", 1, 2, 9, 10, 11, 12, 13, 14));
        step2.forEach(
            p -> {
                PzcActivityGroup pzcActivityGroup = pzcActivityGroupMapper.selectById(p.getGroupId());
                PzcUser other = pzcUserMapper.selectById(p.getUserId());
                PzcUser my = pzcUserMapper.selectById(pzcActivityGroup.getUserId());
                p.setOtherMoney(pzcActivityGroup.getMoney());
                p.setOtherName(other.getNickname());
                p.setOtherAvatar(other.getAvatar());
                p.setOtherUserId(String.valueOf(other.getUserId()));
                p.setOtherLevel(Math.toIntExact(other.getUserLevel()));
                p.setMyAvatar(my.getAvatar());
                p.setTitle(pzcActivityGroup.getTitle());
            }
        );
        List<PzcActivityGroupApply> result = new java.util.ArrayList<>();
        result.addAll(step1);
        result.addAll(step2);


        //按照更新时间倒序排列
        List<PzcActivityGroupApply> collect = result.stream().sorted((o1, o2) -> o2.getUpdateTime().compareTo(o1.getUpdateTime())).collect(Collectors.toList());

        return R.ok(collect);
    }

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
     * <p>
     * 1 做校验
     * 1.1 活动是否存在 1.2 活动是否已经开始 1.3 活动是否已经结束 1.4 活动是否已经满员
     * 2 组是否还存在
     *
     *
     * ====================
     * 用户申请活动的时候 判断 是否足够缴纳保证金
     */
    @Log(title = "活动组队申请列表", businessType = BusinessType.INSERT) //
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody PzcActivityGroupApplyBo bo) {
        if (!activityUtils.allCheck(Math.toIntExact(bo.getActivityId()), bo.getGroupId())) {
            return R.fail("申请失败，活动不存在或者已经结束或者组不存在");
        }
        bo.setUserId(LoginHelper.getUserId());
        if (iPzcActivityGroupApplyService.queryByUserIdAndActivityId(bo.getUserId(), bo.getActivityId())) {
            return R.fail("申请失败，您已经申请过了");
        }

        //======================================================
        PzcUser applyUser = pzcUserMapper.selectById(bo.getUserId()); //我有2个币  申请需要 两个币  我则需要 根据当前他的余额来判断
        if(applyUser.getMoney().compareTo(bo.getMoney())<0)
        {
            return R.fail("申请失败，您的余额不足");
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
        if (!activityUtils.allCheck(Math.toIntExact(bo.getActivityId()), bo.getGroupId())) {
            return R.fail("修改失败，活动不存在或者已经结束或者组不存在");
        }
        if (!iPzcActivityGroupApplyService.queryByUserIdAndActivityId(bo.getUserId(), bo.getActivityId())) {
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
