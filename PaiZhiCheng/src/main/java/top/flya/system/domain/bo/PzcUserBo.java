package top.flya.system.domain.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.flya.common.core.domain.BaseEntity;
import top.flya.common.core.validate.AddGroup;
import top.flya.common.core.validate.EditGroup;

import javax.validation.constraints.*;

import java.math.BigDecimal;

/**
 * 用户业务对象 pzc_user
 *
 * @author ruoyi
 * @date 2023-07-04
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class PzcUserBo extends BaseEntity {

    /**
     * 用户主键
     */
    private Integer userId;

    /**
     * 派币余额
     */
    private BigDecimal money;

    /**
     * 昵称
     */
    @NotBlank(message = "昵称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String nickname;

    /**
     * 真实姓名
     */
    @NotBlank(message = "真实姓名不能为空", groups = { AddGroup.class, EditGroup.class })
    private String realname;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 地址
     */
    private String address;

    /**
     * 个人介绍
     */
    private String intro;

    /**
     * 年龄
     */
    private Long age;

    /**
     * 星座
     */
    private String constellation;

    /**
     * 人格类型
     */
    private String mbti;

    /**
     * 兴趣爱好
     */
    private Long hobby;

    /**
     * 学校
     */
    private String school;

    /**
     * 职业
     */
    private String occupation;

    /**
     * 喜欢的音乐风格
     */
    private String musicStyle;

    /**
     * 状态 是否被封禁
     */
    private Long state;


}
