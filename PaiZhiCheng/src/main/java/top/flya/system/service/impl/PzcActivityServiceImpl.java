package top.flya.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import top.flya.common.core.page.TableDataInfo;
import top.flya.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.flya.system.domain.*;
import top.flya.system.domain.bo.PzcActivityBo;
import top.flya.system.domain.vo.PzcActivityVo;
import top.flya.system.mapper.*;
import top.flya.system.service.IPzcActivityService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 活动Service业务层处理
 *
 * @author ruoyi
 * @date 2023-06-02
 */
@RequiredArgsConstructor
@Service
public class PzcActivityServiceImpl implements IPzcActivityService {

    private final PzcActivityMapper baseMapper;

    private final PzcIntroMapper pzcIntroMapper;

    private final PzcActivityConnIntroMapper pzcActivityConnIntroMapper;

    private final PzcArtistMapper pzcArtistMapper;

    private final PzcActivityConnArtistMapper pzcActivityConnArtistMapper;

    private final PzcTagMapper pzcTagMapper;

    private final PzcActivityConnTagMapper pzcActivityConnTagMapper;

    private final PzcOrganizerMapper pzcOrganizerMapper;

    /**
     * 查询活动
     */
    @Override
    public PzcActivityVo queryById(Integer activityId) {
        return baseMapper.selectVoById(activityId);
    }

    /**
     * 查询活动列表
     */
    @Override
    public TableDataInfo<PzcActivityVo> queryPageList(PzcActivityBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PzcActivity> lqw = buildQueryWrapper(bo);
        Page<PzcActivityVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询活动列表
     */
    @Override
    public List<PzcActivityVo> queryList(PzcActivityBo bo) {
        LambdaQueryWrapper<PzcActivity> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<PzcActivity> buildQueryWrapper(PzcActivityBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<PzcActivity> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getAddress()), PzcActivity::getAddress, bo.getAddress());
        lqw.eq(bo.getRegionId() != null, PzcActivity::getRegionId, bo.getRegionId());
        lqw.eq(StringUtils.isNotBlank(bo.getTitle()), PzcActivity::getTitle, bo.getTitle());
        lqw.eq(StringUtils.isNotBlank(bo.getStartTime()), PzcActivity::getStartTime, bo.getStartTime());
        lqw.eq(StringUtils.isNotBlank(bo.getEndDate()), PzcActivity::getEndDate, bo.getEndDate());
        lqw.eq(bo.getSaleEndTime() != null, PzcActivity::getSaleEndTime, bo.getSaleEndTime());
        lqw.eq(StringUtils.isNotBlank(bo.getShowTime()), PzcActivity::getShowTime, bo.getShowTime());
        lqw.eq(StringUtils.isNotBlank(bo.getCoverImage()), PzcActivity::getCoverImage, bo.getCoverImage());
        lqw.between(params.get("beginCreateTime") != null && params.get("endCreateTime") != null,
            PzcActivity::getCreateTime, params.get("beginCreateTime"), params.get("endCreateTime"));
        lqw.between(params.get("beginUpdateTime") != null && params.get("endUpdateTime") != null,
            PzcActivity::getUpdateTime, params.get("beginUpdateTime"), params.get("endUpdateTime"));
        lqw.eq(bo.getState() != null, PzcActivity::getState, bo.getState());
        lqw.eq(bo.getOrganizerList() != null && bo.getOrganizerList().getOrganizerId() != null, PzcActivity::getOrganizerId, bo.getOrganizerList().getOrganizerId());
        return lqw;
    }

    /**
     * 新增活动
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(PzcActivityBo bo) {
        PzcActivity add = BeanUtil.toBean(bo, PzcActivity.class);
        validEntityBeforeSave(add);

        //这里关联其他表的保存
        if (bo.getIntroList().size() != 0) {
            bo.getIntroList().forEach(intro -> {
                //首先查询这个介绍是否存在
                LambdaQueryWrapper<PzcIntro> lqw = Wrappers.lambdaQuery();
                lqw.eq(PzcIntro::getIntroId, intro.getIntroId());
                PzcIntro pzcIntro = pzcIntroMapper.selectOne(lqw);
                if (pzcIntro == null) {
                    throw new RuntimeException("介绍不存在 id is " + intro.getIntroId());
                }
                //介绍表关联活动表 先查询关联关系是否存在
                LambdaQueryWrapper<PzcActivityConnIntro> lqw2 = Wrappers.lambdaQuery();
                lqw2.eq(PzcActivityConnIntro::getActivityId, bo.getActivityId());
                lqw2.eq(PzcActivityConnIntro::getIntroId, intro.getIntroId());
                PzcActivityConnIntro pzcActivityConnIntro1 = pzcActivityConnIntroMapper.selectOne(lqw2);
                if (pzcActivityConnIntro1 != null) {
                    throw new RuntimeException("介绍已经关联 id is " + intro.getIntroId() + "无需重复关联");
                }

                PzcActivityConnIntro pzcActivityConnIntro = new PzcActivityConnIntro();
                pzcActivityConnIntro.setActivityId(bo.getActivityId());
                pzcActivityConnIntro.setIntroId(Math.toIntExact(intro.getIntroId()));
                pzcActivityConnIntroMapper.insert(pzcActivityConnIntro);

            });

        }
        if (bo.getArtistList().size() != 0) {
            bo.getArtistList().forEach(artist -> {
                //首先查询这个艺人是否存在
                LambdaQueryWrapper<PzcArtist> lqw = Wrappers.lambdaQuery();
                lqw.eq(PzcArtist::getArtistId, artist.getArtistId());
                PzcArtist pzcArtist = pzcArtistMapper.selectOne(lqw);
                if (pzcArtist == null) {
                    throw new RuntimeException("艺术家不存在 id is " + artist.getArtistId());
                }
                //介绍表关联活动表 先查询关联关系是否存在
                LambdaQueryWrapper<PzcActivityConnArtist> lqw2 = Wrappers.lambdaQuery();
                lqw2.eq(PzcActivityConnArtist::getActivityId, bo.getActivityId());
                lqw2.eq(PzcActivityConnArtist::getArtistId, artist.getArtistId());
                PzcActivityConnArtist pzcActivityConnArtist1 = pzcActivityConnArtistMapper.selectOne(lqw2);
                if (pzcActivityConnArtist1 != null) {
                    throw new RuntimeException("艺术家已经关联 id is " + artist.getArtistId() + "无需重复关联");
                }

                PzcActivityConnArtist pzcActivityConnArtist = new PzcActivityConnArtist();
                pzcActivityConnArtist.setActivityId(bo.getActivityId());
                pzcActivityConnArtist.setArtistId(Math.toIntExact(artist.getArtistId()));
                pzcActivityConnArtistMapper.insert(pzcActivityConnArtist);

            });

        }
        if (bo.getTagList().size() != 0) {
            bo.getTagList().forEach(tag -> {
                //首先查询这个标签是否存在
                LambdaQueryWrapper<PzcTag> lqw = Wrappers.lambdaQuery();
                lqw.eq(PzcTag::getTagId, tag.getTagId());
                PzcTag pzcTag = pzcTagMapper.selectOne(lqw);
                if (pzcTag == null) {
                    throw new RuntimeException("标签不存在 id is " + tag.getTagId());
                }
                //介绍表关联活动表 先查询关联关系是否存在
                LambdaQueryWrapper<PzcActivityConnTag> lqw2 = Wrappers.lambdaQuery();
                lqw2.eq(PzcActivityConnTag::getActivityId, bo.getActivityId());
                lqw2.eq(PzcActivityConnTag::getTagId, tag.getTagId());
                PzcActivityConnTag pzcActivityConnTag1 = pzcActivityConnTagMapper.selectOne(lqw2);
                if (pzcActivityConnTag1 != null) {
                    throw new RuntimeException("标签已经关联 id is " + tag.getTagId() + "无需重复关联");
                }

                PzcActivityConnTag pzcActivityConnTag = new PzcActivityConnTag();
                pzcActivityConnTag.setActivityId(bo.getActivityId());
                pzcActivityConnTag.setTagId(Math.toIntExact(tag.getTagId()));
                pzcActivityConnTagMapper.insert(pzcActivityConnTag);

            });

        }

        if (bo.getOrganizerList() != null) {

            PzcOrganizer organizer = bo.getOrganizerList();

            //首先查询这个组织是否存在
            LambdaQueryWrapper<PzcOrganizer> lqw = Wrappers.lambdaQuery();
            lqw.eq(PzcOrganizer::getOrganizerId, organizer.getOrganizerId());
            PzcOrganizer pzcOrganizer = pzcOrganizerMapper.selectOne(lqw);
            if (pzcOrganizer == null) {
                throw new RuntimeException("活动组织者不存在 id is " + organizer.getOrganizerId());
            }
            add.setOrganizerId(organizer.getOrganizerId());
        }

        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setActivityId(add.getActivityId());
        }


        return flag;
    }

    /**
     * 修改活动
     */
    @Override
    public Boolean updateByBo(PzcActivityBo bo) {
        PzcActivity update = BeanUtil.toBean(bo, PzcActivity.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(PzcActivity entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除活动
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
