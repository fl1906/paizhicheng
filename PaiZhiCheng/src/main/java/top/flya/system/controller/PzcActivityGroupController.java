package top.flya.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import top.flya.system.domain.*;
import top.flya.system.domain.bo.PzcActivityGroupBo;
import top.flya.system.domain.vo.PzcActivityGroupApplyVo;
import top.flya.system.domain.vo.PzcActivityGroupVo;
import top.flya.system.mapper.*;
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
@Slf4j
@RequestMapping("/system/activityGroup")
public class PzcActivityGroupController extends BaseController {

    private final IPzcActivityGroupService iPzcActivityGroupService;

    private final IPzcActivityGroupApplyService iPzcActivityGroupApplyService;

    private final PzcUserMapper pzcUserMapper;

    private final PzcActivityMapper pzcActivityMapper;

    private final PzcUserPhotoMapper pzcUserPhotoMapper;

    private final WxUtils wxUtils;

    private final PzcActivityGroupMapper pzcActivityGroupMapper;

    private final PzcActivityGroupApplyMapper pzcActivityGroupApplyMapper;

    private final PzcOfficialMapper pzcOfficialMapper;


    @PostMapping("/cancel") //取消 双方都可以取消
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
        // 给对方发官方消息 通知对方已取消
        PzcOfficial pzcOfficial = new PzcOfficial();
        pzcOfficial.setIsRead(0L);


        PzcUser otherUser = null;
        if(pzcActivityGroupVo.getUserId().equals(userId)) //我是申请人 取消
        {
         otherUser = pzcUserMapper.selectById(pzcActivityGroupMapper.selectById(groupId).getUserId());

        }else { //我是发起人
          otherUser = pzcUserMapper.selectById(pzcActivityGroupApplyVo.getUserId());
        }
        pzcOfficial.setToUserId(otherUser.getUserId());
        pzcOfficial.setTitle("来自"+otherUser.getNickname()+"与您的组队信息：");
        pzcOfficial.setContent("在【"+pzcActivityGroupVo.getTitle()+"】组队中途，对方已经取消本次组队。请重新选择队伍进行匹配~");
        pzcOfficial.setGroupId(groupId);
        pzcOfficial.setActivityId(pzcActivityGroupVo.getActivityId());
        pzcOfficialMapper.insert(pzcOfficial);

        Integer applyStatus = pzcActivityGroupApplyVo.getApplyStatus();
        if (applyStatus != 0 && applyStatus != 1 && applyStatus != 9 && applyStatus != 10) {
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
        //修改状态为 已取消
        return R.ok(iPzcActivityGroupApplyService.updateStatus(applyId.longValue(), -1));
    }


    @PostMapping("/cancelByGroupIn") //活动过程中取消组队 扣除保证金 + 0.2派币 退还对方派币 + 对方的保证金 通知对方
    @Transactional
    @RepeatSubmit()
    public R cancelByGroupIn(@RequestParam("applyId") Integer applyId) {
        Long userId = LoginHelper.getUserId();
        PzcUser my = pzcUserMapper.selectById(userId);
        PzcActivityGroupApplyVo pzcActivityGroupApplyVo = iPzcActivityGroupApplyService.queryById(applyId.longValue());
        if (pzcActivityGroupApplyVo == null) {
            return R.fail("申请不存在");
        }
        PzcOfficial pzcOfficial = new PzcOfficial();
        pzcOfficial.setIsRead(0L);
        if (pzcActivityGroupApplyVo.getUserId().equals(userId)) //我是申请方
        {
            //把钱都返还给发起方
            BigDecimal money = pzcActivityGroupApplyVo.getMoney();
            money = money.subtract(new BigDecimal("0.2"));
            PzcActivityGroup pzcActivityGroup = pzcActivityGroupMapper.selectById(pzcActivityGroupApplyVo.getGroupId());
            PzcUser startUser = pzcUserMapper.selectById(pzcActivityGroup.getUserId());
            startUser.setMoney(startUser.getMoney().add(money).add(pzcActivityGroup.getMoney())); //全额返回给发起方的保证金 + 对方扣除 0.2保证金 后的派币
            pzcUserMapper.updateById(startUser);

            pzcOfficial.setTitle("来自"+my.getNickname()+"与您的组队信息：");
            pzcOfficial.setContent("在【"+pzcActivityGroup.getTitle()+"】组队中途，申请方已经取消本次组队。对方的违约金 【"+money+"派币】已纳入您的账户。请查收~");
            pzcOfficial.setToUserId(startUser.getUserId());
            pzcOfficial.setGroupId(pzcActivityGroup.getGroupId());
            pzcOfficial.setActivityId(pzcActivityGroup.getActivityId());
            pzcOfficialMapper.insert(pzcOfficial);

        }else {
            //我是发起方
            PzcUser applyUser = pzcUserMapper.selectById(pzcActivityGroupApplyVo.getUserId());
            PzcActivityGroup pzcActivityGroup = pzcActivityGroupMapper.selectById(pzcActivityGroupApplyVo.getGroupId());
            applyUser.setMoney(applyUser.getMoney().
                add(pzcActivityGroup.getMoney().subtract(new BigDecimal("0.2")))
                .add(pzcActivityGroupApplyVo.getMoney())); //全额返回给申请方的保证金
            pzcUserMapper.updateById(applyUser);

            pzcOfficial.setTitle("来自"+my.getNickname()+"与您的组队信息：");
            pzcOfficial.setContent("在【"+pzcActivityGroup.getTitle()+"】组队中途，发起方已经取消本次组队。对方的违约金 【"+pzcActivityGroup.getMoney().subtract(new BigDecimal("0.2"))+"派币】已纳入您的账户。请查收~");
            pzcOfficial.setToUserId(applyUser.getUserId());
            pzcOfficial.setGroupId(pzcActivityGroup.getGroupId());
            pzcOfficial.setActivityId(pzcActivityGroup.getActivityId());
            pzcOfficialMapper.insert(pzcOfficial);
        }
        return R.ok(iPzcActivityGroupApplyService.updateStatus(applyId.longValue(), -1));//修改状态为 已取消
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
     *
     * @param applyId
     * @return
     */

    @PostMapping("/pj") //双方评价 （可选）
    @Transactional
    public R pj(@RequestParam("applyId") Integer applyId, @RequestParam("score") Integer score) {
        wxUtils.checkApplyScore(score);
        PzcActivityGroupApplyVo pzcActivityGroupApplyVo = wxUtils.checkApplyPj(applyId.longValue());
        //首先获取我的UserId
        Long userId = LoginHelper.getUserId();
        Long groupId = pzcActivityGroupApplyVo.getGroupId();
        Integer applyStatus = pzcActivityGroupApplyVo.getApplyStatus();
        if (pzcActivityGroupApplyVo.getUserId().equals(userId)) {
            //获取对方 userId 并且修改对方积分
            Long otherUserId = pzcActivityGroupMapper.selectById(groupId).getUserId();
            PzcUser otherUser = pzcUserMapper.selectById(otherUserId);
            if (applyStatus == 14) {
                return R.fail("您已经评价过了 不可重复操作");
            }
            otherUser.setIntegration(otherUser.getIntegration() + score);
            otherUser.setIntegrationNow(otherUser.getIntegrationNow() + score);
            wxUtils.updateUserMsg(otherUser);

            pzcUserMapper.updateById(otherUser);

            if (applyStatus == 13) //发起方评价了
            {
                return R.ok(iPzcActivityGroupApplyService.updateStatus(applyId.longValue(), 15)); //双方都已评价
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
        if (applyStatus == 13) {
            return R.fail("您已经评价过了 不可重复操作");
        }

        // ============================================================================================
        Long otherUserId = pzcActivityGroupApplyVo.getUserId();
        PzcUser otherUser = pzcUserMapper.selectById(otherUserId);
        otherUser.setIntegration(otherUser.getIntegration() + score);
        otherUser.setIntegrationNow(otherUser.getIntegrationNow() + score);
        wxUtils.updateUserMsg(otherUser);
        pzcUserMapper.updateById(otherUser);


        //看看申请方是否评价了
        if (applyStatus == 14) //申请方评价了
        {
            return R.ok(iPzcActivityGroupApplyService.updateStatus(applyId.longValue(), 15)); //双方都已评价
        }

        return R.ok(iPzcActivityGroupApplyService.updateStatus(applyId.longValue(), 13));//发起方评价

    }


    /**
     * 双方都到达了目的地 开始扣手续费
     *
     * @param applyId
     * @return
     */
    @PostMapping("/confirmReach") //确认到达目的地
    @Transactional
    public R confirmReach(@RequestParam("applyId") Integer applyId) {
        PzcActivityGroupApplyVo pzcActivityGroupApplyVo = wxUtils.checkApplyConfirm(applyId.longValue());
        Long userId = LoginHelper.getUserId();
        Long groupId = pzcActivityGroupApplyVo.getGroupId();
        Integer applyStatus = pzcActivityGroupApplyVo.getApplyStatus();
        if (applyStatus != 2 && applyStatus != 11 && applyStatus != 12) {
            return R.fail("该订单目前状态为【" + wxUtils.applyStatus(applyStatus) + "】不可确认");
        }

        //获取发起方的保证金
        PzcActivityGroupVo pzcActivityGroupVo = iPzcActivityGroupService.queryById(groupId);

        if (pzcActivityGroupApplyVo.getUserId().equals(userId)) { //我是申请方
            if (applyStatus == 11) //发起方确认了
            {

                BigDecimal money = pzcActivityGroupVo.getMoney();
                money = money.subtract(new BigDecimal("0.1"));
                //将保证金还给发起方
                PzcUser pzcUser = pzcUserMapper.selectById(pzcActivityGroupVo.getUserId());
                pzcUser.setMoney(pzcUser.getMoney().add(money));
                pzcUserMapper.updateById(pzcUser);
                //========================================================================
                //获取申请方的保证金
                PzcUser applyUser = pzcUserMapper.selectById(pzcActivityGroupApplyVo.getUserId());
                BigDecimal applyMoney = pzcActivityGroupApplyVo.getMoney();
                applyMoney = applyMoney.subtract(new BigDecimal("0.1"));
                //将保证金还给申请方
                applyUser.setMoney(applyUser.getMoney().add(applyMoney));
                pzcUserMapper.updateById(applyUser);

                return R.ok(iPzcActivityGroupApplyService.updateStatus(applyId.longValue(), 3)); //双方都已确认
            }
            if (applyStatus == 12) {
                return R.fail("您已经确认过了 不可重复操作");
            }
            return R.ok(iPzcActivityGroupApplyService.updateStatus(applyId.longValue(), 12));//申请方确认
        }
        //判断当前 用户是否为组队发起人 如果不是 直接报错
        if (pzcActivityGroupVo == null) {
            return R.fail("组队不存在");
        }
        if (!pzcActivityGroupVo.getUserId().equals(userId)) {
            return R.fail("你不是组队发起人");
        }
        //看看申请方是否确认了
        if (applyStatus == 12) //我是发起方
        {
            //获取发起方的保证金
            BigDecimal money = pzcActivityGroupVo.getMoney();
            money = money.subtract(new BigDecimal("0.1"));
            //将保证金还给发起方
            PzcUser pzcUser = pzcUserMapper.selectById(pzcActivityGroupVo.getUserId());
            pzcUser.setMoney(pzcUser.getMoney().add(money));
            pzcUserMapper.updateById(pzcUser);
            //========================================================================
            //获取申请方的保证金
            PzcUser applyUser = pzcUserMapper.selectById(pzcActivityGroupApplyVo.getUserId());
            BigDecimal applyMoney = pzcActivityGroupApplyVo.getMoney();
            applyMoney = applyMoney.subtract(new BigDecimal("0.1"));
            //将保证金还给申请方
            applyUser.setMoney(applyUser.getMoney().add(applyMoney));
            pzcUserMapper.updateById(applyUser);


            return R.ok(iPzcActivityGroupApplyService.updateStatus(applyId.longValue(), 3)); //双方都已确认
        }
        if (applyStatus == 11) {
            return R.fail("您已经确认过了 不可重复操作");
        }
        return R.ok(iPzcActivityGroupApplyService.updateStatus(applyId.longValue(), 11));//发起方确认
    }


    @GetMapping("/applyRole")
    public R applyRole(@RequestParam("applyId")Integer applyId )
    {
        Long userId = LoginHelper.getUserId();
        PzcActivityGroupApplyVo pzcActivityGroupApplyVo = wxUtils.checkApplyConfirm(applyId.longValue());
        if (pzcActivityGroupApplyVo.getUserId().equals(userId))
        {
            return R.ok(1); //我是申请方
        }else {
            return R.ok(0); //我是发起方
        }
    }


    @PostMapping("/confirm") //确认申请 (这里判断 保证金 是否足够缴纳 保证 新人卡 bug)
    @Transactional
    public R confirm(@RequestParam("applyId") Integer applyId) {
        PzcActivityGroupApplyVo pzcActivityGroupApplyVo = wxUtils.checkApplyConfirm(applyId.longValue());
        Long userId = LoginHelper.getUserId();
        Long groupId = pzcActivityGroupApplyVo.getGroupId();
        Integer applyStatus = pzcActivityGroupApplyVo.getApplyStatus();

        if (applyStatus == 2) {
            return R.fail("该订单进行中，不可确认");
        }
        PzcActivityGroupVo group = iPzcActivityGroupService.queryById(groupId);
        //获取双方保证金
        BigDecimal applyMoney = pzcActivityGroupApplyVo.getMoney();
        BigDecimal startMoney = group.getMoney();
        //获取双方
        PzcUser applyUser = pzcUserMapper.selectById(pzcActivityGroupApplyVo.getUserId());
        PzcUser startUser = pzcUserMapper.selectById(group.getUserId());

        //如果可以确认 判断 是那一方确认的
        if (pzcActivityGroupApplyVo.getUserId().equals(userId)) {// 自己是申请方
            if (applyStatus == 10) {
                return R.fail("您已经确认过了 不可重复操作");
            }

            //如果是自己确认的 则修改状态为 已确认
            if (applyStatus == 9) //发起方确认了
            {
                log.info("申请方的余额与保证金 {}----{}", applyUser.getMoney(), applyMoney);
                // 如果有一方保证金不足以缴纳 则报错
                if (applyUser.getMoney().compareTo(applyMoney) < 0) {
                    return R.fail("您的保证金不足以缴纳");
                }


                //双方都已确认 开始 扣除保证金
                //获取申请方保证金
                applyUser.setMoney(applyUser.getMoney().subtract(applyMoney));
                pzcUserMapper.updateById(applyUser);
                //获取发起方保证金
                startUser.setMoney(startUser.getMoney().subtract(startMoney));
                pzcUserMapper.updateById(startUser);

                return R.ok(iPzcActivityGroupApplyService.updateStatus(applyId.longValue(), 2)); //双方都已确认
            }


            return R.ok(iPzcActivityGroupApplyService.updateStatus(applyId.longValue(), 10));//申请方确认
        } else { //自己是发起方
            //判断当前 用户是否为组队发起人 如果不是 直接报错‘
            PzcActivityGroupVo pzcActivityGroupVo = iPzcActivityGroupService.queryById(groupId);
            if (pzcActivityGroupVo == null) {
                return R.fail("组队不存在");
            }
            if (!pzcActivityGroupVo.getUserId().equals(userId)) {
                return R.fail("你不是组队发起人");
            }
            if (applyStatus == 9) {
                return R.fail("您已经确认过了 不可重复操作");
            }

            //看看申请方是否确认了
            if (applyStatus == 10) //申请方确认了
            {

                log.info("发起方的余额与保证金 {}----{}", startUser.getMoney(), startMoney);
                if (startUser.getMoney().compareTo(startMoney) < 0) {
                    return R.fail("您的的保证金不足以缴纳");
                }

                //双方都已确认 开始 扣除保证金
                //获取申请方保证金
                applyUser.setMoney(applyUser.getMoney().subtract(applyMoney));
                pzcUserMapper.updateById(applyUser);
                //获取发起方保证金
                startUser.setMoney(startUser.getMoney().subtract(startMoney));
                pzcUserMapper.updateById(startUser);
                return R.ok(iPzcActivityGroupApplyService.updateStatus(applyId.longValue(), 2)); //双方都已确认
            }
            return R.ok(iPzcActivityGroupApplyService.updateStatus(applyId.longValue(), 9));//发起方确认
        }
    }


    /**
     * 同意用户申请 进入下一阶段
     * 同意用户申请时 先判断对方是否  处于组队进程
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
        PzcUser my = pzcUserMapper.selectById(userId);

        Long groupId = pzcActivityGroupApplyVo.getGroupId();
        PzcActivityGroupVo pzcActivityGroupVo = iPzcActivityGroupService.queryById(groupId);
        if (pzcActivityGroupVo == null) {
            return R.fail("组队不存在");
        }
        if (!pzcActivityGroupVo.getUserId().equals(userId)) {
            return R.fail("你不是组队发起人");
        }
        //判断对方是否 处于 组队进程 如果是 则不可同意
        Long applyUserId = pzcActivityGroupApplyVo.getUserId();
        //获取活动Id
        Long activityId = pzcActivityGroupVo.getActivityId();
        //获取活动组队列表 除了自己这个队伍外
        List<PzcActivityGroup> groups = pzcActivityGroupMapper.selectList(new QueryWrapper<PzcActivityGroup>().eq("activity_id", activityId));
        List<Long> groupIds = groups.stream().filter(s -> !s.getGroupId().equals(groupId)).map(PzcActivityGroup::getGroupId).collect(Collectors.toList());
        //然后获取当前对方申请了几个队伍 判断每个队伍的进程 如果有 进程处于 组队状态中 则不可以同意
        List<PzcActivityGroupApply> applies = pzcActivityGroupApplyMapper.selectList(new QueryWrapper<PzcActivityGroupApply>().in("group_id", groupIds).eq("user_id", applyUserId));
        applies.forEach(
            a -> {
                if (a.getApplyStatus() != 0 && a.getApplyStatus() != 3 && a.getApplyStatus() != 13 && a.getApplyStatus() != 14 && a.getApplyStatus() != 15) {
                    throw new RuntimeException("该用户已经处于组队进程中 等待对方结束活动再试哦");
                }
            }
        );

        //修改状态为 已同意
        Integer integer = iPzcActivityGroupApplyService.updateStatus(applyId, 1);
        if (integer == null || integer != 1) {
            return R.fail("修改状态失败");
        }

        //给对方发消息 已经同意了对方的申请 请尽快确认
        PzcOfficial pzcOfficial = new PzcOfficial();
        pzcOfficial.setIsRead(0L);
        PzcUser otherUser = pzcUserMapper.selectById(applyUserId);
        pzcOfficial.setToUserId(otherUser.getUserId());
        pzcOfficial.setTitle("来自"+my.getNickname()+"与您的组队信息：");
        pzcOfficial.setContent("您的组队申请已经被对方同意，请尽快确认~");
        pzcOfficial.setGroupId(groupId);
        pzcOfficial.setActivityId(activityId);
        pzcOfficialMapper.insert(pzcOfficial);

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
