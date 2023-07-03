package top.flya.system.service;

import top.flya.system.domain.PzcUser;
import top.flya.system.domain.vo.PzcUserVo;
import top.flya.system.domain.bo.PzcUserBo;
import top.flya.common.core.page.TableDataInfo;
import top.flya.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 用户Service接口
 *
 * @author ruoyi
 * @date 2023-07-04
 */
public interface IPzcUserService {

    /**
     * 查询用户
     */
    PzcUserVo queryById(Integer userId);

    /**
     * 查询用户列表
     */
    TableDataInfo<PzcUserVo> queryPageList(PzcUserBo bo, PageQuery pageQuery);

    /**
     * 查询用户列表
     */
    List<PzcUserVo> queryList(PzcUserBo bo);

    /**
     * 新增用户
     */
    Boolean insertByBo(PzcUserBo bo);

    /**
     * 修改用户
     */
    Boolean updateByBo(PzcUserBo bo);

    /**
     * 校验并批量删除用户信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);
}
