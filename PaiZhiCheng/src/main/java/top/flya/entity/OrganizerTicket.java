package top.flya.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OrganizerTicket {
    @JsonProperty("id")
    private int id;  // ID

    @JsonProperty("name")
    private String name;  // 名称

    @JsonProperty("start_date")
    private String startDate;  // 开始日期

    @JsonProperty("cover_image")
    private String coverImage;  // 封面图片
}
