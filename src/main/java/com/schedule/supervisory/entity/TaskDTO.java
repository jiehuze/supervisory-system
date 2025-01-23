package com.schedule.supervisory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("task")
public class TaskDTO {
    private Long id;
    private String source;
    private String content;
    private String leading_official; // 使用下划线命名法
    private String leading_department; // 使用下划线命名法
    private LocalDate deadline;
    private LocalDateTime created_at; // 使用下划线命名法
    private String progress;
    private String issues_and_challenges; // 使用下划线命名法
    private Boolean requires_coordination; // 使用下划线命名法
    private String approval_status; // 使用下划线命名法
    private Integer status;
    private Boolean is_urgent; // 使用下划线命名法
}
