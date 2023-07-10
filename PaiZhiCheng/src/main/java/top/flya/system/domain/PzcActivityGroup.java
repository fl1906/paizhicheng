package top.flya.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import top.flya.common.core.domain.BaseEntity;

/**
 * 活动组队对象 pzc_activity_group
 *
 * @author ruoyi
 * @date 2023-07-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pzc_activity_group")
public class PzcActivityGroup extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 组队ID

     */
    @TableId(value = "group_id",type = IdType.AUTO)
    private Long groupId;
    /**
     * 活动ID
     */
    private Long activityId;
    /**
     * 活动组队发起人ID
     */
    private Long userId;
    /**
     * 活动主题
     */
    private String title;
    /**
     * 活动组队所缴纳的保证金
     */
    private BigDecimal money;
    /**
     * 买单方式
     */
    private Long groupType;
    /**
     * 活动地址
     */
    private String address;
    /**
     * 一起约定好的时间
     */
    private Date activityTime;
    /**
     * 权限
     */
    private Long auth;

    @TableField(exist = false)
    private PzcUser user;

}
