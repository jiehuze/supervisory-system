package com.schedule.supervisory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data // Lombok annotation to generate getters and setters
@TableName("task")
public class Task {

    @TableId(type = IdType.AUTO)
    private Long id; // 对应数据库列 'id'
    private String source; // 对应数据库列 'source'
    private String content; // 对应数据库列 'content'
    private String leadingOfficial; // 对应数据库列 'leading_official'
    private String leadingDepartment; // 对应数据库列 'leading_department'
    private LocalDate deadline; // 对应数据库列 'deadline'
    private LocalDateTime createdAt; // 对应数据库列 'created_at'
    private String progress; // 对应数据库列 'progress'
    private String issuesAndChallenges; // 对应数据库列 'issues_and_challenges'
    private Boolean requiresCoordination; // 对应数据库列 'requires_coordination'
    private String approvalStatus; // 对应数据库列 'approval_status'
    /*
    * 状态定义：
    * 1：待接收；
    * 2：正常推进；
    * 3：已超期；
    * 4：审核中（承包领导）
    * 5：审核中（交办人）
    * 6：完成
    * */
    private Integer status; // 对应数据库列 'status'
    private Boolean isUrgent; // 对应数据库列 'is_urgent'
    private Boolean isReview; // 对应数据库列 'is_review'
    private String nextSteps; // 对应数据库列 'next_steps'
    private String handler; // 对应数据库列 'handler'
    private String phone; // 对应数据库列 'phone'
    private LocalDateTime updatedAt; // 对应数据库列 'updated_at'
}