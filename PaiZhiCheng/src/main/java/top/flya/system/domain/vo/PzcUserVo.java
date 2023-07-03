package top.flya.system.domain.vo;

import java.math.BigDecimal;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import top.flya.common.annotation.ExcelDictFormat;
import top.flya.common.convert.ExcelDictConvert;
import lombok.Data;


/**
 * 用户视图对象 pzc_user
 *
 * @author ruoyi
 * @date 2023-07-04
 */
@Data
@ExcelIgnoreUnannotated
public class PzcUserVo {

    private static final long serialVersionUID = 1L;

    /**
     * 用户主键
     */
    @ExcelProperty(value = "用户主键")
    private Integer userId;

    /**
     * 派币余额
     */
    @ExcelProperty(value = "派币余额")
    private BigDecimal money;

    /**
     * 昵称
     */
    @ExcelProperty(value = "昵称")
    private String nickname;

    /**
     * 真实姓名
     */
    @ExcelProperty(value = "真实姓名")
    private String realname;

    /**
     * 手机号
     */
    @ExcelProperty(value = "手机号")
    private String phone;

    /**
     * 头像
     */
    @ExcelProperty(value = "头像")
    private String avatar;

    /**
     * 地址
     */
    @ExcelProperty(value = "地址")
    private String address;

    /**
     * 个人介绍
     */
    @ExcelProperty(value = "个人介绍")
    private String intro;

    /**
     * 年龄
     */
    @ExcelProperty(value = "年龄")
    private Long age;

    /**
     * 星座
     */
    @ExcelProperty(value = "星座", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "constellation")
    private String constellation;

    /**
     * 人格类型
     */
    @ExcelProperty(value = "人格类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "mbti")
    private String mbti;

    /**
     * 兴趣爱好
     */
    @ExcelProperty(value = "兴趣爱好")
    private Long hobby;

    /**
     * 学校
     */
    @ExcelProperty(value = "学校")
    private String school;

    /**
     * 职业
     */
    @ExcelProperty(value = "职业")
    private String occupation;

    /**
     * 喜欢的音乐风格
     */
    @ExcelProperty(value = "喜欢的音乐风格", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "music_style")
    private String musicStyle;

    /**
     * 状态 是否被封禁
     */
    @ExcelProperty(value = "状态 是否被封禁", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "state")
    private Long state;


}
