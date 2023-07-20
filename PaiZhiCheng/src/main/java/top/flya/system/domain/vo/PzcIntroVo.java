package top.flya.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 活动介绍视图对象 pzc_intro
 *
 * @author ruoyi
 * @date 2023-06-01
 */
@Data
@ExcelIgnoreUnannotated
@AllArgsConstructor
@NoArgsConstructor
public class PzcIntroVo {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ExcelProperty(value = "ID")
    private Long introId;

    /**
     * 内容
     */
    @ExcelProperty(value = "内容")
    private String content;

    /**
     * 活动介绍 可放图片
     */
    @ExcelProperty(value = "活动介绍 可放图片")
    private String imageFullUrl;


    /**
     * 介绍的主标题
     */
    @ExcelProperty(value = "介绍的主标题")
    private String title;

}
