package top.flya.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import top.flya.common.annotation.ExcelDictFormat;
import top.flya.common.convert.ExcelDictConvert;
import lombok.Data;

import java.util.Date;


/**
 * 活动视图对象 pzc_activity
 *
 * @author ruoyi
 * @date 2023-06-02
 */
@Data
@ExcelIgnoreUnannotated
public class PzcActivityVo {

    private static final long serialVersionUID = 1L;

    /**
     * 活动id
     */
    @ExcelProperty(value = "活动id")
    private Integer activityId;

    /**
     * 地址
     */
    @ExcelProperty(value = "地址")
    private String address;

    /**
     * 城市ID
     */
    @ExcelProperty(value = "城市ID")
    private Integer regionId;

    /**
     * 活动标题
     */
    @ExcelProperty(value = "活动标题")
    private String title;

    /**
     * 开始时间
     */
    @ExcelProperty(value = "开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @ExcelProperty(value = "结束时间")
    private String endDate;

    /**
     * 销售结束时间
     */
    @ExcelProperty(value = "销售结束时间")
    private Long saleEndTime;

    /**
     * 展示时间
     */
    @ExcelProperty(value = "展示时间")
    private String showTime;

    /**
     * 封面图片
     */
    @ExcelProperty(value = "封面图片")
    private String coverImage;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ExcelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 删除状态，默认为1表示正常状态
     */
    @ExcelProperty(value = "删除状态，默认为1表示正常状态")
    private Integer state;


}
