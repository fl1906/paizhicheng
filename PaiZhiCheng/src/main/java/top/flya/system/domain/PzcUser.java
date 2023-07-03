package top.flya.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.flya.common.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 用户对象 pzc_user
 *
 * @author ruoyi
 * @date 2023-07-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pzc_user")
public class PzcUser extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 用户主键
     */
    @TableId(value = "user_id",type = IdType.AUTO)
    private Integer userId;
    /**
     * 派币余额
     */
    private BigDecimal money;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 真实姓名
     */
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
