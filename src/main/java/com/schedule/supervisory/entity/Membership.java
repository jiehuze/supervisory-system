package com.schedule.supervisory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data // Lombok annotation to generate getters and setters
@TableName("membership")
public class Membership {
    private Integer id;
    private String leadingDepartment;
    private String leadingDepartmentId;
    private String responsiblePerson;
    private String responsiblePersonId;
    private Integer priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}