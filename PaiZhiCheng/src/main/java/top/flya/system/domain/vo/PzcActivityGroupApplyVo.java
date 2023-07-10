package top.flya.system.domain.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import top.flya.common.annotation.ExcelDictFormat;
import top.flya.common.convert.ExcelDictConvert;
import lombok.Data;


/**
 * 活动组队申请列视图对象 pzc_activity_group_apply
 *
 * @author ruoyi
 * @date 2023-07-10
 */
@Data
@ExcelIgnoreUnannotated
public class PzcActivityGroupApplyVo {

    private static final long serialVersionUID = 1L;

    /**
     * 申请ID
     */
    @ExcelProperty(value = "申请ID")
    private Long applyId;

    /**
     * 申请人ID
     */
    @ExcelProperty(value = "申请人ID")
    private Long userId;

    /**
     * 申请的活动ID
     */
    @ExcelProperty(value = "申请的活动ID")
    private Long activityId;

    /**
     * 申请加入的组ID
     */
    @ExcelProperty(value = "申请加入的组ID")
    private Long groupId;

    /**
     * 0 AA制
1 我买单
2 你买单
     */
    @ExcelProperty(value = "0 AA制 1 我买单 2 你买单", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "group_pay_type")
    private Long groupType;

    /**
     * 活动保证金
     */
    @ExcelProperty(value = "活动保证金")
    private BigDecimal money;

    /**
     * 留言内容
     */
    @ExcelProperty(value = "留言内容")
    private String message;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Date createTime;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Date updateTime;


}
