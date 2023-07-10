package top.flya.system.domain.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import top.flya.common.core.domain.BaseEntity;
import top.flya.common.core.validate.AddGroup;
import top.flya.common.core.validate.EditGroup;

/**
 * 活动组队业务对象 pzc_activity_group
 *
 * @author ruoyi
 * @date 2023-07-10
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class PzcActivityGroupBo extends BaseEntity {

    /**
     * 组队ID

     */
    @NotNull(message = "组队ID不能为空", groups = { EditGroup.class })
    private Long groupId;

    /**
     * 活动ID
     */
    @NotNull(message = "活动ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long activityId;

    /**
     * 活动组队发起人ID
     */
    @NotNull(message = "活动组队发起人ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 活动主题
     */
    @NotBlank(message = "活动主题不能为空", groups = { AddGroup.class, EditGroup.class })
    private String title;

    /**
     * 活动组队所缴纳的保证金
     */
    @NotNull(message = "活动组队所缴纳的保证金不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal money;

    /**
     * 买单方式
     */
    @NotNull(message = "买单方式不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long groupType;

    /**
     * 活动地址
     */
    @NotBlank(message = "活动地址不能为空", groups = { AddGroup.class, EditGroup.class })
    private String address;

    /**
     * 一起约定好的时间
     */
    @NotNull(message = "一起约定好的时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date activityTime;

    /**
     * 权限
     */
    @NotNull(message = "权限不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long auth;


}
