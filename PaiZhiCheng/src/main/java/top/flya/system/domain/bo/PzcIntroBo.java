package top.flya.system.domain.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.flya.common.core.domain.BaseEntity;
import top.flya.common.core.validate.AddGroup;
import top.flya.common.core.validate.EditGroup;

import javax.validation.constraints.*;


/**
 * 活动介绍业务对象 pzc_intro
 *
 * @author ruoyi
 * @date 2023-06-01
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class PzcIntroBo extends BaseEntity {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空", groups = { EditGroup.class })
    private Long introId;

    /**
     * 内容
     */
    @NotBlank(message = "内容不能为空", groups = { AddGroup.class, EditGroup.class })
    private String content;

    /**
     * 活动介绍 可放图片
     */
    @NotBlank(message = "活动介绍 可放图片不能为空", groups = { AddGroup.class, EditGroup.class })
    private String imageFullUrl;

    /**
     * 介绍的主标题
     */
    @NotBlank(message = "介绍的主标题不能为空", groups = { AddGroup.class, EditGroup.class })
    private String title;


}
