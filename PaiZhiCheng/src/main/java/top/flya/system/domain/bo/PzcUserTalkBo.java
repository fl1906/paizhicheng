package top.flya.system.domain.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.flya.common.core.domain.BaseEntity;
import top.flya.common.core.validate.AddGroup;
import top.flya.common.core.validate.EditGroup;

import javax.validation.constraints.*;
import java.io.Serializable;


/**
 * 用户聊天业务对象 pzc_user_talk
 *
 * @author ruoyi
 * @date 2023-07-14
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class PzcUserTalkBo extends BaseEntity implements Serializable {

    /**
     * 聊天ID
     */
    @NotNull(message = "聊天ID不能为空", groups = { EditGroup.class })
    private Integer talkId;

    /**
     * 发起方
     */
    @NotNull(message = "发起方不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long fromUserId;

    /**
     * 接受方
     */
    @NotNull(message = "接受方不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long toUserId;

    /**
     * 聊天内容
     */
    @NotBlank(message = "聊天内容不能为空", groups = { AddGroup.class, EditGroup.class })
    private String message;

    /**
     * 内容类型
     */
    @NotNull(message = "内容类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long messageType;


}
