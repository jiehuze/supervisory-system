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
    private String source; // 任务来源 'source'
    private String content; // 任务内容 'content'
    private String leadingOfficial; // 牵头区领导 'leading_official'
    private String leadingOfficialId; // 牵头区领导id
    private String leadingDepartment; // 牵头单位 'leading_department'
    private String leadingDepartmentId; // 牵头单位ID 'leading_department_id'
    private LocalDate deadline; // 完成时间 'deadline'
    private LocalDateTime createdAt; // 对应数据库列 'created_at'
    private String progress; // 具体进展 'progress'
    private String issuesAndChallenges; // 存在的问题或者困难 'issues_and_challenges'
    private Boolean requiresCoordination; // 是否需要领导协调解决 'requires_coordination'
    private String instruction; // 批示 'approval_status'
    /*
     * 状态定义：
     * 1：待接收；
     * 2：正常推进；
     * 3：已超期；
     * 4：审核中（承办领导）
     * 5：审核中（交办人）
     * 6：已完成
     * 7：取消审核中（承办领导）
     * 8：取消审核中（交办人）
     * 9：已取消
     * */
    private Integer status; // 任务状态 'status'
    private Integer checkStatus;
    private Integer overdueDays; // 新增逾期天数字段，当状态为3时，计算超期天数
    private Boolean isUrgent; //  是否催办 'is_urgent'
    private Boolean isReview; // 是否需要承办领导审核 'is_review'
    private String nextSteps; // 下一个阶段 'next_steps'
    private String handler; // 经办人 'handler'
    private String handlerId; //经办人id
    private String phone; // 电话 'phone'
    private LocalDateTime updatedAt; // 更新时间 'updated_at'
    private LocalDateTime completedAt; // 办结时间 'updated_at'
    private LocalDate sourceDate; // 来源时间（到天）对应数据库列 'source_date'
    private String responsiblePerson; // 责任人对应数据库列 'responsible_person'
    private String responsiblePersonId; // 责任人ID 'responsible_person_id'
    //1:短期；2：中期；3：长期
    private Integer taskPeriod; // 任务周期（int类型）对应数据库列 'task_period'
    private Integer fieldId; // 所属领域（int类型）对应数据库列 'field_id'
    private String coOrganizer; // 协办单位对应数据库列 'co_organizer'
    private String coOrganizerId; // 协办单位id对应数据库列 'co_organizer'
    private String cbDoneDesc; // 承办人办结申请描述对应数据库列 'cb_done_desc'
    private String cbDoneFile; // 申请文件对应数据库列 'cb_done_file'
    private Integer closureReviewResult; // 办结审核结果对应数据库列 'closure_review_result'
    private String closureReviewDesc; // 办结审核描述对应数据库列 'closure_review_desc'
    private String closureReviewFile; // 办结审核文件对应数据库列 'closure_review_file'
    private String cancelDesc; // 取消描述
    private String cancelFile; // 取消文件
    private String assigner; //交办人
    private String assignerId; //交办人Id
    private String tbFileUrl; //同步文件url
    private Integer fillCycle; //填报周期
}