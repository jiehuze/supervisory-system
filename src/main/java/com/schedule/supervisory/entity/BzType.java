package com.schedule.supervisory.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("public.bz_type")
public class BzType {
    @TableId
    private Integer id;

    private String name;

    private String description;

    private String type;

    private Integer typeId;

    private Date createdAt;

}