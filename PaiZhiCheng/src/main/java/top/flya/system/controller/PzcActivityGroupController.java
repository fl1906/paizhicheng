package top.flya.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
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
import top.flya.system.domain.PzcUser;
import top.flya.system.domain.PzcUserPhoto;
import top.flya.system.domain.bo.PzcActivityGroupBo;
import top.flya.system.domain.vo.PzcActivityGroupApplyVo;
import top.flya.system.domain.vo.PzcActivityGroupVo;
import top.flya.system.mapper.PzcActivityMapper;
import top.flya.system.mapper.PzcUserMapper;
import top.flya.system.mapper.PzcUserPhotoMapper;
import top.flya.system.service.IPzcActivityGroupApplyService;
import top.flya.system.service.IPzcActivityGroupService;
import top.flya.system.utils.WxUtils;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    private final IPzcActivityGroupApplyService iPzcActivityGroupApplyService;

    private final PzcUserMapper pzcUserMapper;

    private final PzcActivityMapper pzcActivityMapper;

    private final PzcUserPhotoMapper pzcUserPhotoMapper;

    private final WxUtils wxUtils;


    @PostMapping("/cancel")
    @Transactional
    @RepeatSubmit()
    public R cancel(@RequestParam("applyId") Integer applyId) {

        //首先查询这个组队是否是我发起的
        PzcActivityGroupApplyVo pzcActivityGroupApplyVo = iPzcActivityGroupApplyService.queryById(applyId.longValue());
        if (pzcActivityGroupApplyVo == null) {
            return R.fail("申请不存在");
        }
        Long userId = LoginHelper.getUserId();
        Long groupId = pzcActivityGroupApplyVo.getGroupId();
        PzcActivityGroupVo pzcActivityGroupVo = iPzcActivityGroupService.queryById(groupId);
        if (pzcActivityGroupVo == null) {
            return R.fail("组队不存在");
        }
        if (!pzcActivityGroupVo.getUserId().equals(userId)) {
            return R.fail("你不是组队发起人");
        }

        Integer applyStatus = pzcActivityGroupApplyVo.getApplyStatus();
        if (applyStatus == -1 || applyStatus == 2 || applyStatus == 3) {
            return R.fail("该订单位于【" + wxUtils.applyStatus(applyStatus) + "】状态，不可取消");
        }

        //查询用户是否有免责取消次数
        PzcUser pzcUser = pzcUserMapper.selectById(LoginHelper.getUserId());
        if (pzcUser.getExemptCancel() > 0) {
            pzcUser.setExemptCancel(pzcUser.getExemptCancel() - 1);
        } else {
            pzcUser.setMoney(pzcUser.getMoney().subtract(new BigDecimal("0.1"))); //扣除0.1派币
        }
        pzcUserMapper.updateById(pzcUser);
        //TODO 给对方发官方消息 通知对方已取消

        //修改状态为 已取消
        return R.ok(iPzcActivityGroupApplyService.updateStatus(applyId.longValue(), -1));
    }


    /**
     * 我创建的活动的申请列表
     * 思路整理
     * 首先查出所有 GroupId
     * 然后查出groupId 对应的申请列表
     *
     * @return
     */
    @GetMapping("/applyList")
    public R<List<PzcActivityGroupApplyVo>> applyList() {
        PzcActivityGroupBo bo = new PzcActivityGroupBo();
        bo.setUserId(LoginHelper.getUserId());
        List<PzcActivityGroupVo> pzcActivityGroupVos = iPzcActivityGroupService.queryList(bo);
        List<Long> groupIds = pzcActivityGroupVos.stream().map(PzcActivityGroupVo::getGroupId).collect(Collectors.toList());
        if (groupIds.size() == 0) {
            return R.ok();
        }

        List<PzcActivityGroupApplyVo> pzcActivityGroupApplyVos = iPzcActivityGroupApplyService.queryListByGroupIds(groupIds);
        pzcActivityGroupApplyVos.forEach(
            s -> {
                PzcUser pzcUser = pzcUserMapper.selectById(s.getUserId());
                s.setNickName(pzcUser.getNickname());
                s.setAvatar(pzcUser.getAvatar());
                s.setActivityTitle(pzcActivityMapper.selectVoById(s.getActivityId()).getTitle());
                s.setGroupTitle(pzcActivityGroupVos.stream().filter(s1 -> s1.getGroupId().equals(s.getGroupId())).findFirst().get().getTitle());
            }
        );
        List<PzcActivityGroupApplyVo> result = pzcActivityGroupApplyVos.stream().filter(s -> s.getApplyStatus() != -1).collect(Collectors.toList());//过滤掉已取消的
        return R.ok(result);
    }

    @GetMapping("/userInfo") //查看申请人或者发起人信息
    public R userInfo(@RequestParam("userId") Long userId, @RequestParam("groupId") Long groupId) {
        //首先查询该用户是否申请了我的组 申请了 才有资格去查看
        PzcActivityGroupApplyVo pzcActivityGroupApplyVo = iPzcActivityGroupApplyService.queryByUserIdAndGroupId(userId, groupId);
        if (pzcActivityGroupApplyVo == null) {
            return R.fail("该用户未申请你的组");
        }
        PzcUser pzcUser = pzcUserMapper.selectById(userId);
        pzcUser.setMoney(null);
        pzcUser.setUserPhoto(pzcUserPhotoMapper.selectList(new QueryWrapper<>(new PzcUserPhoto()).eq("user_id", userId)));
        pzcUser.setPzcActivityGroupApplyVo(pzcActivityGroupApplyVo);

        return R.ok(pzcUser);
    }

    /**
     * 13 发起方已评价
     * 14 申请方已评价
     * 15 双方已评价
     * @param applyId
     * @return
     */

    @PostMapping("/pj") //双方评价 （可选）
    public R pj(@RequestParam("applyId")Integer applyId)
    {
        PzcActivityGroupApplyVo pzcActivityGroupApplyVo = wxUtils.checkApplyPj(applyId.longValue());
       //首先获取我的UserId
        Long userId = LoginHelper.getUserId();
        Long groupId = pzcActivityGroupApplyVo.getGroupId();
        Integer applyStatus = pzcActivityGroupApplyVo.getApplyStatus();
        if (pzcActivityGroupApplyVo.getUserId().equals(userId)) {
            if (applyStatus == 13) //发起方评价了
            {
                return R.ok(iPzcActivityGroupApplyService.updateStatus(applyId.longValue(), 15)); //双方都已评价
            }
            if (applyStatus == 14) {
                return R.fail("您已经评价过了 不可重复操作");
            }
            return R.ok(iPzcActivityGroupApplyService.updateStatus(applyId.longValue(), 14));//申请方评价
        }
        //判断当前 用户是否为组队发起人 如果不是 直接报错
        PzcActivityGroupVo pzcActivityGroupVo = iPzcActivityGroupService.queryById(groupId);
        if (pzcActivityGroupVo == null) {
            return R.fail("组队不存在");
        }
        if (!pzcActivityGroupVo.getUserId().equals(userId)) {
            return R.fail("你不是组队发起人");
        }
        //看看申请方是否评价了
        if (applyStatus == 14) //申请方评价了
        {
            return R.ok(iPzcActivityGroupApplyService.updateStatus(applyId.longValue(), 15)); //双方都已评价
        }
        if (applyStatus == 13) {
            return R.fail("您已经评价过了 不可重复操作");
        }
        return R.ok(iPzcActivityGroupApplyService.updateStatus(applyId.longValue(), 13));//发起方评价

    }


    @PostMapping("/confirmReach") //确认到达目的地
    public R confirmReach(@RequestParam("applyId") Integer applyId) {
        PzcActivityGroupApplyVo pzcActivityGroupApplyVo = wxUtils.checkApplyConfirm(applyId.longValue());
        Long userId = LoginHelper.getUserId();
        Long groupId = pzcActivityGroupApplyVo.getGroupId();
        Integer applyStatus = pzcActivityGroupApplyVo.getApplyStatus();
        if(applyStatus!=2&&applyStatus!=11&&applyStatus!=12)
        {
            return R.fail("该订单目前状态为【"+wxUtils.applyStatus(applyStatus)+"】不可确认");
        }
        if (pzcActivityGroupApplyVo.getUserId().equals(userId)) {
            if (applyStatus == 11) //发起方确认了
            {
                return R.ok(iPzcActivityGroupApplyService.updateStatus(applyId.longValue(), 3)); //双方都已确认
            }
            if (applyStatus == 12) {
                return R.fail("您已经确认过了 不可重复操作");
            }
            return R.ok(iPzcActivityGroupApplyService.updateStatus(applyId.longValue(), 12));//申请方确认
        }
        //判断当前 用户是否为组队发起人 如果不是 直接报错
        PzcActivityGroupVo pzcActivityGroupVo = iPzcActivityGroupService.queryById(groupId);
        if (pzcActivityGroupVo == null) {
            return R.fail("组队不存在");
        }
        if (!pzcActivityGroupVo.getUserId().equals(userId)) {
            return R.fail("你不是组队发起人");
        }
        //看看申请方是否确认了
        if (applyStatus == 12) //申请方确认了
        {
            return R.ok(iPzcActivityGroupApplyService.updateStatus(applyId.longValue(), 3)); //双方都已确认
        }
        if (applyStatus == 11) {
            return R.fail("您已经确认过了 不可重复操作");
        }
        return R.ok(iPzcActivityGroupApplyService.updateStatus(applyId.longValue(), 11));//发起方确认
    }


    @PostMapping("/confirm") //确认申请
    public R confirm(@RequestParam("applyId") Integer applyId) {
        PzcActivityGroupApplyVo pzcActivityGroupApplyVo = wxUtils.checkApplyConfirm(applyId.longValue());
        Long userId = LoginHelper.getUserId();
        Long groupId = pzcActivityGroupApplyVo.getGroupId();
        Integer applyStatus = pzcActivityGroupApplyVo.getApplyStatus();

        if(applyStatus==2)
        {
            return R.fail("该订单进行中，不可确认");
        }
        //如果可以确认 判断 是那一方确认的
        if (pzcActivityGroupApplyVo.getUserId().equals(userId)) {
            //如果是自己确认的 则修改状态为 已确认
            if (applyStatus == 9) //发起方确认了
            {
                return R.ok(iPzcActivityGroupApplyService.updateStatus(applyId.longValue(), 2)); //双方都已确认
            }
            if (applyStatus == 10) {
                return R.fail("您已经确认过了 不可重复操作");
            }
            return R.ok(iPzcActivityGroupApplyService.updateStatus(applyId.longValue(), 10));//申请方确认
        } else {
            //判断当前 用户是否为组队发起人 如果不是 直接报错‘
            PzcActivityGroupVo pzcActivityGroupVo = iPzcActivityGroupService.queryById(groupId);
            if (pzcActivityGroupVo == null) {
                return R.fail("组队不存在");
            }
            if (!pzcActivityGroupVo.getUserId().equals(userId)) {
                return R.fail("你不是组队发起人");
            }
            //看看申请方是否确认了
            if (applyStatus == 10) //申请方确认了
            {
                return R.ok(iPzcActivityGroupApplyService.updateStatus(applyId.longValue(), 2)); //双方都已确认
            }
            if (applyStatus == 9) {
                return R.fail("您已经确认过了 不可重复操作");
            }
            return R.ok(iPzcActivityGroupApplyService.updateStatus(applyId.longValue(), 9));//发起方确认
        }
    }


    /**
     * 同意用户申请 进入下一阶段
     *
     * @return
     */
    @PostMapping("/apply")
    public R apply(@RequestParam("applyId") Long applyId) {
        //首先查询这个组队是否是我发起的
        PzcActivityGroupApplyVo pzcActivityGroupApplyVo = iPzcActivityGroupApplyService.queryById(applyId);
        if (pzcActivityGroupApplyVo == null) {
            return R.fail("申请不存在");
        }
        Long userId = LoginHelper.getUserId();

        Long groupId = pzcActivityGroupApplyVo.getGroupId();
        PzcActivityGroupVo pzcActivityGroupVo = iPzcActivityGroupService.queryById(groupId);
        if (pzcActivityGroupVo == null) {
            return R.fail("组队不存在");
        }
        if (!pzcActivityGroupVo.getUserId().equals(userId)) {
            return R.fail("你不是组队发起人");
        }
        //修改状态为 已同意
        Integer integer = iPzcActivityGroupApplyService.updateStatus(applyId, 1);
        if (integer == null || integer != 1) {
            return R.fail("修改状态失败");
        }

        return R.ok();
    }


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
