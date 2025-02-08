package com.schedule.supervisory.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("bz_form")
public class BzForm {
    @TableId
    private Long id; // 表id
    private String name; // 名称
    private String type; // 类型
    private Integer typeId; // 类型id
    private Integer gear; // 档位
    private String creator; // 创建人
    private String creatorId; // 创建人id
    private java.sql.Timestamp createdAt; // 创建时间
    private java.sql.Timestamp updatedAt; // 更新时间
}