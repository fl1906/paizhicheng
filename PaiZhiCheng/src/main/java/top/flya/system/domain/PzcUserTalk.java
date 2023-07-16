package top.flya.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.flya.common.core.domain.BaseEntity;


/**
 * 用户聊天对象 pzc_user_talk
 *
 * @author ruoyi
 * @date 2023-07-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pzc_user_talk")
public class PzcUserTalk extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 聊天ID
     */
    @TableId(value = "talk_id",type = IdType.AUTO)
    private Integer talkId;
    /**
     * 发起方
     */
    private Long fromUserId;
    /**
     * 接受方
     */
    private Long toUserId;
    /**
     * 聊天内容
     */
    private String message;
    /**
     * 内容类型
     */
    private Long messageType;

}
