package com.schedule.supervisory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data // Lombok annotation to generate getters and setters
@TableName("membership")
public class Membership {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String leadingDepartment;
    private String leadingDepartmentId;
    private String responsiblePerson;
    private String responsiblePersonId;
    private Integer priority;
    private String phone; // 电话 'phone'
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}