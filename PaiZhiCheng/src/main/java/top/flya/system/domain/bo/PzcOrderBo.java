package top.flya.system.domain.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.flya.common.core.domain.BaseEntity;
import top.flya.common.core.validate.AddGroup;
import top.flya.common.core.validate.EditGroup;

import javax.validation.constraints.*;


/**
 * 订单业务对象 pzc_order
 *
 * @author ruoyi
 * @date 2023-07-09
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class PzcOrderBo extends BaseEntity {

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { EditGroup.class })
    private Long orderId;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 活动ID
     */
    @NotNull(message = "活动ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long activityId;

    /**
     * 订单状态
     */
    @NotNull(message = "订单状态不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long status;

    /**
     * 订单类型
     */
    @NotNull(message = "订单类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long type;

    /**
     * 外部订单号
     */
    @NotBlank(message = "外部订单号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String outOrderNum;

    /**
     * 描述
     */
    @NotBlank(message = "描述不能为空", groups = { AddGroup.class, EditGroup.class })
    private String intro;

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空", groups = { AddGroup.class, EditGroup.class })
    private String title;


}
