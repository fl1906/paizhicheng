package top.flya.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import top.flya.common.core.page.TableDataInfo;
import top.flya.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.flya.system.domain.PzcUserPhoto;
import top.flya.system.domain.bo.PzcActivityGroupBo;
import top.flya.system.domain.vo.PzcActivityGroupVo;
import top.flya.system.domain.PzcActivityGroup;
import top.flya.system.mapper.PzcActivityGroupMapper;
import top.flya.system.mapper.PzcActivityMapper;
import top.flya.system.mapper.PzcUserPhotoMapper;
import top.flya.system.service.IPzcActivityGroupService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 活动组队Service业务层处理
 *
 * @author ruoyi
 * @date 2023-07-10
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class PzcActivityGroupServiceImpl implements IPzcActivityGroupService {

    private final PzcActivityGroupMapper baseMapper;

    private final PzcActivityMapper  pzcActivityMapper;

    private final PzcUserPhotoMapper pzcUserPhotoMapper;

    /**
     * 查询活动组队
     */
    @Override
    public PzcActivityGroupVo queryById(Long groupId){
        PzcActivityGroupVo pzcActivityGroupVo = baseMapper.selectVoByIdDIY(groupId);

        if(pzcActivityGroupVo.getAuth()==2)
        {
            log.info("私密组队，不返回用户信息");
            pzcActivityGroupVo.setUser(null);
            pzcActivityGroupVo.setPhoto(null);
        }else {
            List<PzcUserPhoto> userPhotos = pzcUserPhotoMapper.selectList(new QueryWrapper<PzcUserPhoto>().eq("user_id", pzcActivityGroupVo.getUserId()));
            pzcActivityGroupVo.setPhoto(userPhotos);
            if(pzcActivityGroupVo.getAuth()==1) //权限 1只返回一张图片
            {
                pzcActivityGroupVo.setPhoto(userPhotos.size()>=1? Collections.singletonList(userPhotos.get(0)):null);
            }
        }

        return pzcActivityGroupVo;
    }

    /**
     * 查询活动组队列表
     */
    @Override
    public TableDataInfo<PzcActivityGroupVo> queryPageList(PzcActivityGroupBo bo, PageQuery pageQuery) {
        Page<PzcActivityGroupVo> result = baseMapper.selectDetailsList(pageQuery.build(), bo);
        result.getRecords().forEach(
                pzcActivityGroupVo -> {
                    if (pzcActivityGroupVo.getAuth() == 2) {
                        log.info("私密组队，不返回用户信息");
                        pzcActivityGroupVo.setUser(null);
                    }//如果是私密组队，不返回用户信息
                }
        );

        return TableDataInfo.build(result);
    }

    /**
     * 查询活动组队列表
     */
    @Override
    public List<PzcActivityGroupVo> queryList(PzcActivityGroupBo bo) {
        LambdaQueryWrapper<PzcActivityGroup> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<PzcActivityGroup> buildQueryWrapper(PzcActivityGroupBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<PzcActivityGroup> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getActivityId() != null, PzcActivityGroup::getActivityId, bo.getActivityId());
        lqw.eq(bo.getUserId() != null, PzcActivityGroup::getUserId, bo.getUserId());
        lqw.eq(StringUtils.isNotBlank(bo.getTitle()), PzcActivityGroup::getTitle, bo.getTitle());
        lqw.eq(bo.getMoney() != null, PzcActivityGroup::getMoney, bo.getMoney());
        lqw.eq(bo.getGroupType() != null, PzcActivityGroup::getGroupType, bo.getGroupType());
        lqw.eq(StringUtils.isNotBlank(bo.getAddress()), PzcActivityGroup::getAddress, bo.getAddress());
        lqw.eq(bo.getActivityTime() != null, PzcActivityGroup::getActivityTime, bo.getActivityTime());
        lqw.eq(bo.getAuth() != null, PzcActivityGroup::getAuth, bo.getAuth());
        lqw.eq(bo.getCreateTime() != null, PzcActivityGroup::getCreateTime, bo.getCreateTime());
        lqw.eq(bo.getUpdateTime() != null, PzcActivityGroup::getUpdateTime, bo.getUpdateTime());
        return lqw;
    }

    /**
     * 新增活动组队
     */
    @Override
    public Boolean insertByBo(PzcActivityGroupBo bo) {
        PzcActivityGroup add = BeanUtil.toBean(bo, PzcActivityGroup.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setGroupId(add.getGroupId());
        }
        return flag;
    }

    /**
     * 修改活动组队
     */
    @Override
    public Boolean updateByBo(PzcActivityGroupBo bo) {
        PzcActivityGroup update = BeanUtil.toBean(bo, PzcActivityGroup.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(PzcActivityGroup entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除活动组队
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public boolean checkActivity(Long activityId) {
        return pzcActivityMapper.selectVoById(activityId) != null;
    }
}
