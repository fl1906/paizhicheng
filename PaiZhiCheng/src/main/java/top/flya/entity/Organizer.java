package top.flya.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Organizer {
    @JsonProperty("organizer_tickets")
    private List<OrganizerTicket> organizerTickets;  // 组织者票务列表

    @JsonProperty("id")
    private int id;  // ID

    @JsonProperty("phone")
    private String phone;  // 电话号码

    @JsonProperty("name")
    private String name;  // 名称

    @JsonProperty("logo")
    private String logo;  // 组织者标志图片

}
