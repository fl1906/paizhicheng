package top.flya.system.domain.vo;

import java.math.BigDecimal;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import top.flya.common.annotation.ExcelDictFormat;
import top.flya.common.convert.ExcelDictConvert;
import lombok.Data;


/**
 * 用户视图对象 pzc_user
 *
 * @author ruoyi
 * @date 2023-07-06
 */
@Data
@ExcelIgnoreUnannotated
@AllArgsConstructor
@NoArgsConstructor
public class PzcUserVo {

    private static final long serialVersionUID = 1L;

    /**
     * 用户主键
     */
    @ExcelProperty(value = "用户主键")
    private Integer userId;

    /**
     * OpenId
     */
    @ExcelProperty(value = "OpenId")
    private String openid;

    /**
     * 派币余额
     */
    @ExcelProperty(value = "派币余额")
    private BigDecimal money;

    /**
     * 真实姓名
     */
    @ExcelProperty(value = "真实姓名")
    private String realname;

    /**
     * 昵称
     */
    @ExcelProperty(value = "昵称")
    private String nickname;

    /**
     * 性别
     */
    @ExcelProperty(value = "性别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_user_sex")
    private Integer sex;

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
     * 介绍
     */
    @ExcelProperty(value = "介绍")
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
     * 音乐风格
     */
    @ExcelProperty(value = "音乐风格", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "music_style")
    private String musicStyle;

    /**
     * 封禁状态
     */
    @ExcelProperty(value = "封禁状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "state")
    private Integer state;


}