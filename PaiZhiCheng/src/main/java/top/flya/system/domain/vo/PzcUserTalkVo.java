package top.flya.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import top.flya.common.annotation.ExcelDictFormat;
import top.flya.common.convert.ExcelDictConvert;
import lombok.Data;

import java.util.Date;


/**
 * 用户聊天视图对象 pzc_user_talk
 *
 * @author ruoyi
 * @date 2023-07-14
 */
@Data
@ExcelIgnoreUnannotated
public class PzcUserTalkVo {

    private static final long serialVersionUID = 1L;

    /**
     * 聊天ID
     */
    @ExcelProperty(value = "聊天ID")
    private Integer talkId;

    /**
     * 发起方
     */
    @ExcelProperty(value = "发起方")
    private Long fromUserId;

    /**
     * 接受方
     */
    @ExcelProperty(value = "接受方")
    private Long toUserId;

    /**
     * 聊天内容
     */
    @ExcelProperty(value = "聊天内容", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "user_talk_msg_type")
    private String message;

    /**
     * 内容类型
     */
    @ExcelProperty(value = "内容类型")
    private Long messageType;

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


}
