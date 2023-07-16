package top.flya.system.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import top.flya.system.domain.PzcUserTalk;
import top.flya.system.domain.vo.PzcUserTalkVo;
import top.flya.common.core.mapper.BaseMapperPlus;

/**
 * 用户聊天Mapper接口
 *
 * @author ruoyi
 * @date 2023-07-16
 */
public interface PzcUserTalkMapper extends BaseMapperPlus<PzcUserTalkMapper, PzcUserTalk, PzcUserTalkVo> {

    Page<PzcUserTalkVo> selectVoPageV1(Page<Object> build, @Param("userId") Long userId);

    Integer selectNotReadCount(@Param("fromUserId") Long userId, @Param("toUserId") Long toUserId);
}
