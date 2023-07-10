package top.flya.system.domain.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;


/**
 * 用户资料相册业务对象 pzc_user_photo
 *
 * @author ruoyi
 * @date 2023-07-11
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class PzcUserPhotoBo extends BaseEntity {

    /**
     * 照片ID
     */
    @NotNull(message = "照片ID不能为空", groups = { EditGroup.class })
    private Long photoId;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 照片
     */
    @NotBlank(message = "照片不能为空", groups = { AddGroup.class, EditGroup.class })
    private String url;

    /**
     * 照片说明
     */
    @NotBlank(message = "照片说明不能为空", groups = { AddGroup.class, EditGroup.class })
    private String message;


}
