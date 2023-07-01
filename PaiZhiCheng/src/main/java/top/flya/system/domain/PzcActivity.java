package top.flya.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.flya.common.core.domain.BaseEntity;


/**
 * 活动对象 pzc_activity
 *
 * @author ruoyi
 * @date 2023-06-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pzc_activity")
public class PzcActivity extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 活动id
     */
    @TableId(value = "activity_id", type = IdType.AUTO)
    private Integer activityId;
    /**
     * 地址
     */
    private String address;
    /**
     * 城市ID
     */
    private Integer regionId;
    /**
     * 活动标题
     */
    private String title;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endDate;
    /**
     * 销售结束时间
     */
    private Long saleEndTime;
    /**
     * 展示时间
     */
    private String showTime;
    /**
     * 封面图片
     */
    private String coverImage;
    /**
     * 删除状态，默认为1表示正常状态
     */
    private Integer state;

    private Integer classify; //属于哪个分类

    private Integer region; // 0 国内 1 国外


    private Long organizerId; //主办方id

}
