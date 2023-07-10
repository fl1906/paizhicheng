package top.flya.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 用户资料相册对象 pzc_user_photo
 *
 * @author ruoyi
 * @date 2023-07-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pzc_user_photo")
public class PzcUserPhoto extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 照片ID
     */
    @TableId(value = "photo_id")
    private Long photoId;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 照片
     */
    private String url;
    /**
     * 照片说明
     */
    private String message;

}
