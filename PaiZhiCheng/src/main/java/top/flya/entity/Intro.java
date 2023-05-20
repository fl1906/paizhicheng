package top.flya.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Intro {
    @JsonProperty("image")
    private String image;  // 图片

    @JsonProperty("content")
    private String content;  // 内容

    @JsonProperty("image_full_url")
    private String imageFullUrl;  // 完整图片URL

}
