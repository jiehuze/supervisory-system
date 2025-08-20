package com.schedule.supervisory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("consultation") // 映射数据库表名
public class Consultation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String content;

    private String person;

    private String phone;

    private LocalDateTime createTime;

    private String leadingDepartment;

    private String expert;
}