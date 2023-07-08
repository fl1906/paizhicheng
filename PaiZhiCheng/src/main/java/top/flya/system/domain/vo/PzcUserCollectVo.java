package top.flya.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import top.flya.common.annotation.ExcelDictFormat;
import top.flya.common.convert.ExcelDictConvert;
import lombok.Data;


/**
 * 用户收藏活动视图对象 pzc_user_collect
 *
 * @author ruoyi
 * @date 2023-07-08
 */
@Data
@ExcelIgnoreUnannotated
public class PzcUserCollectVo {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ExcelProperty(value = "ID")
    private Long collectId;

    /**
     * 用户Id
     */
    @ExcelProperty(value = "用户Id")
    private Long userId;

    /**
     * 收藏活动的Id
     */
    @ExcelProperty(value = "收藏活动的Id")
    private Long activityId;


}
