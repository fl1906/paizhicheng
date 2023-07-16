package top.flya.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import top.flya.common.core.page.TableDataInfo;
import top.flya.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.flya.system.domain.bo.PzcUserTalkBo;
import top.flya.system.domain.vo.PzcUserTalkVo;
import top.flya.system.domain.PzcUserTalk;
import top.flya.system.mapper.PzcUserTalkMapper;
import top.flya.system.service.IPzcUserTalkService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 用户聊天Service业务层处理
 *
 * @author ruoyi
 * @date 2023-07-14
 */
@RequiredArgsConstructor
@Service
public class PzcUserTalkServiceImpl implements IPzcUserTalkService {

    private final PzcUserTalkMapper baseMapper;

    /**
     * 查询用户聊天
     */
    @Override
    public PzcUserTalkVo queryById(Integer talkId){
        return baseMapper.selectVoById(talkId);
    }

    /**
     * 查询用户聊天列表
     */
    @Override
    public TableDataInfo<PzcUserTalkVo> queryPageList(PzcUserTalkBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PzcUserTalk> lqw = buildQueryWrapper(bo);
        Page<PzcUserTalkVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询用户聊天列表
     */
    @Override
    public List<PzcUserTalkVo> queryList(PzcUserTalkBo bo) {
        LambdaQueryWrapper<PzcUserTalk> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<PzcUserTalk> buildQueryWrapper(PzcUserTalkBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<PzcUserTalk> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getFromUserId() != null, PzcUserTalk::getFromUserId, bo.getFromUserId());
        lqw.eq(bo.getToUserId() != null, PzcUserTalk::getToUserId, bo.getToUserId());
        lqw.eq(StringUtils.isNotBlank(bo.getMessage()), PzcUserTalk::getMessage, bo.getMessage());
        lqw.eq(bo.getMessageType() != null, PzcUserTalk::getMessageType, bo.getMessageType());
        lqw.eq(bo.getCreateTime() != null, PzcUserTalk::getCreateTime, bo.getCreateTime());
        lqw.eq(bo.getUpdateTime() != null, PzcUserTalk::getUpdateTime, bo.getUpdateTime());
        return lqw;
    }

    /**
     * 新增用户聊天
     */
    @Override
    public Boolean insertByBo(PzcUserTalkBo bo) {
        PzcUserTalk add = BeanUtil.toBean(bo, PzcUserTalk.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setTalkId(add.getTalkId());
        }
        return flag;
    }

    /**
     * 修改用户聊天
     */
    @Override
    public Boolean updateByBo(PzcUserTalkBo bo) {
        PzcUserTalk update = BeanUtil.toBean(bo, PzcUserTalk.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(PzcUserTalk entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除用户聊天
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
