package top.flya.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true) //活动
public class Activity extends FLBaseEntity{

    @TableId(value = "activity_id", type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private String activityId; //活动id

    private String address;  // 地址

    private Integer regionId;  // 城市ID

    private String title; //活动标题

    private String startTime;  // 开始时间

    private long saleEndTime;  // 销售结束时间

    private String showTime;  // 展示时间

    @TableField(exist = false)
    private List<Intro> introList;  // 简介列表

    @TableField(exist = false) //标签列表
    private List<Tag> tagList;
}
