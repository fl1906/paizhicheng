package top.flya.system.controller;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.corundumstudio.socketio.SocketIOClient;
import lombok.RequiredArgsConstructor;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
import top.flya.system.config.ClientCache;
import top.flya.system.domain.vo.PzcUserTalkVo;
import top.flya.system.domain.bo.PzcUserTalkBo;
import top.flya.system.handel.MessageEventHandler;
import top.flya.system.service.IPzcUserTalkService;
import top.flya.common.core.page.TableDataInfo;

import static top.flya.system.config.ClientCache.concurrentHashMap;

/**
 * 用户聊天
 *
 * @author ruoyi
 * @date 2023-07-14
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/userTalk")
@Slf4j
public class PzcUserTalkController extends BaseController {

    private final IPzcUserTalkService iPzcUserTalkService;


    /**
     * 用户在线状态
     * @param userId
     * @return
     */
    @GetMapping("/live")
    public R liveStatus(@RequestParam("userId")String userId)
    {
        if(concurrentHashMap.get(userId)==null)
        {
            return R.fail("当前用户不在线");
        }
        return R.ok("当前用户在线");
    }


    /**
     * 查询用户聊天列表
     */
    @GetMapping("/list")
    public TableDataInfo<PzcUserTalkVo> list(PzcUserTalkBo bo, PageQuery pageQuery) {
        return iPzcUserTalkService.queryPageList(bo, pageQuery);
    }


    /**
     * 我的聊天列表
     *
     * 1. 按照 最后聊天的时间倒序排列
     * 2. 展示 最新一条聊天记录 以及未读消息条数
     */


//
//    /**
//     * 导出用户聊天列表
//     */
//    @Log(title = "用户聊天", businessType = BusinessType.EXPORT)
//    @PostMapping("/export")
//    public void export(PzcUserTalkBo bo, HttpServletResponse response) {
//        List<PzcUserTalkVo> list = iPzcUserTalkService.queryList(bo);
//        ExcelUtil.exportExcel(list, "用户聊天", PzcUserTalkVo.class, response);
//    }

    /**
     * 获取用户聊天详细信息
     *
     * @param talkId 主键
     */
    @GetMapping("/{talkId}")
    public R<PzcUserTalkVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Integer talkId) {
        return R.ok(iPzcUserTalkService.queryById(talkId));
    }

//    /**
//     * 新增用户聊天
//     */
//    @Log(title = "用户聊天", businessType = BusinessType.INSERT)
//    @RepeatSubmit()
//    @PostMapping()
//    public R<Void> add(@Validated(AddGroup.class) @RequestBody PzcUserTalkBo bo) {
//        return toAjax(iPzcUserTalkService.insertByBo(bo));
//    }

//    /**
//     * 修改用户聊天
//     */
//    @Log(title = "用户聊天", businessType = BusinessType.UPDATE)
//    @RepeatSubmit()
//    @PutMapping()
//    public R<Void> edit(@Validated(EditGroup.class) @RequestBody PzcUserTalkBo bo) {
//        return toAjax(iPzcUserTalkService.updateByBo(bo));
//    }

//    /**
//     * 删除用户聊天
//     *
//     * @param talkIds 主键串
//     */
//    @Log(title = "用户聊天", businessType = BusinessType.DELETE)
//    @DeleteMapping("/{talkIds}")
//    public R<Void> remove(@NotEmpty(message = "主键不能为空")
//                          @PathVariable Integer[] talkIds) {
//        return toAjax(iPzcUserTalkService.deleteWithValidByIds(Arrays.asList(talkIds), true));
//    }
}
