package com.schedule.supervisory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("public.bz_type")
public class BzType {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private String description;

    private String type;

    private Integer typeId;

    private boolean delete;

    private Integer orderNum; //排序

    private Date createdAt;

}