package top.flya.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.flya.common.core.domain.BaseEntity;


/**
 * 订单对象 pzc_order
 *
 * @author ruoyi
 * @date 2023-07-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pzc_order")
public class PzcOrder extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     *
     */
    @TableId(value = "order_id",type = IdType.AUTO)
    private Long orderId;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 活动ID
     */
    private Long activityId;
    /**
     * 订单状态
     */
    private Long status;
    /**
     * 订单类型
     */
    private Long type;
    /**
     * 外部订单号
     */
    private String outOrderNum;
    /**
     * 描述
     */
    private String intro;
    /**
     * 标题
     */
    private String title;

}
