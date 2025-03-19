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
    /**
     * 任务类型：
     * 0：督查室任务（现在用的）；
     * 1： 我的交办和承办任务
     */
    private Integer taskType;
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
     * 状态定义：优化前
     * 1：待接收；
     * 2：正常推进；
     * 3：已超期；
     * 4：审核中（承办领导）
     * 5：审核中（交办人）
     * 6：已完成
     * 7：取消审核中（承办领导）
     * 8：取消审核中（交办人）
     * 9：已终止
     * 状态定义：优化后
     * 1：待接收
     * 2：正常推进
     * 6：已完成
     * 9：已终止
     * 12：审核中
     * */
    private Integer status; // 任务状态 'status'
    //    private String check;
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
    //1:短期；2：中期；3：长期；4：大于6个月
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
    private String undertaker; //承办人
    private String undertakerId; //承办人id
    private String tbFileUrl; //同步文件url
    private Integer fillCycle; //填报周期
    private String checkStatus; //审核状态 1: 任务审核；2：阶段性审核；3：报表审核；4：指标审核，用逗号分割
    private String processInstanceId; //审核流水号
    private String processInstanceReportId; //填报审核流水号
    private String processInstanceReviewIds; //审核的人，使用逗号分割
    private Boolean delete = false;  // 默认值为false

    private String deleteBy;

    private LocalDateTime deleteAt;

    private String operator;

    private LocalDateTime operationAt;
}