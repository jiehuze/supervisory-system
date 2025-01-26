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
    * 6：已完成
    * */
    private Integer status; // 对应数据库列 'status'
    private Boolean isUrgent; // 对应数据库列 'is_urgent'
    private Boolean isReview; // 对应数据库列 'is_review'
    private String nextSteps; // 对应数据库列 'next_steps'
    private String handler; // 对应数据库列 'handler'
    private String phone; // 对应数据库列 'phone'
    private LocalDateTime updatedAt; // 对应数据库列 'updated_at'
    private LocalDate sourceDate; // 来源时间（到天）对应数据库列 'source_date'
    private String responsiblePerson; // 责任人对应数据库列 'responsible_person'
    private Integer taskPeriod; // 任务周期（int类型）对应数据库列 'task_period'
    private Integer fieldId; // 所属领域（int类型）对应数据库列 'field_id'
    private String coOrganizer; // 协办单位对应数据库列 'co_organizer'
    private String cbDoneDesc; // 承办人办结申请描述对应数据库列 'cb_done_desc'
    private String cbDoneFile; // 申请文件对应数据库列 'cb_done_file'
    private Integer closureReviewResult; // 办结审核结果对应数据库列 'closure_review_result'
    private String closureReviewDesc; // 办结审核描述对应数据库列 'closure_review_desc'
    private String closureReviewFile; // 办结审核文件对应数据库列 'closure_review_file'
}