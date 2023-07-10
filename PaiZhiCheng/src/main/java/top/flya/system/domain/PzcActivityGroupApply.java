package top.flya.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.flya.common.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 活动组队申请列对象 pzc_activity_group_apply
 *
 * @author ruoyi
 * @date 2023-07-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pzc_activity_group_apply")
public class PzcActivityGroupApply extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 申请ID
     */
    @TableId(value = "apply_id",type = IdType.AUTO)
    private Long applyId;
    /**
     * 申请人ID
     */
    private Long userId;
    /**
     * 申请的活动ID
     */
    private Long activityId;
    /**
     * 申请加入的组ID
     */
    private Long groupId;
    /**
     * 0 AA制
1 我买单
2 你买单
     */
    private Long groupType;
    /**
     * 活动保证金
     */
    private BigDecimal money;
    /**
     * 留言内容
     */
    private String message;

}
