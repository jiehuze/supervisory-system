package com.schedule.supervisory.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TaskSearchDTO implements Serializable {
    private String source; // 任务来源 'source'
    private String content; // 任务内容 'content'
    private String leadingOfficial; // 牵头区领导 'leading_official'
    private String leadingOfficialId; // 牵头区领导id
    private String leadingDepartment; // 牵头单位 'leading_department'
    private String leadingDepartmentId; // 牵头单位ID 'leading_department_id'
    private LocalDate deadline; // 完成时间 'deadline'
    private Integer status; // 任务状态 'status'
    private Integer overdueDays; // 新增逾期天数字段，当状态为3时，计算超期天数
    private LocalDate sourceDate; // 来源时间（到天）对应数据库列 'source_date'
    private String responsiblePerson; // 责任人对应数据库列 'responsible_person'
    private String responsiblePersonId; // 责任人ID 'responsible_person_id'
    private Integer taskPeriod; // 任务周期（int类型）对应数据库列 'task_period'

    private String userId;
    private String coOrganizerId;
    private LocalDateTime createdAtStart;
    private LocalDateTime createdAtEnd;
    private Boolean unfinished;
    private Boolean unAuth; //不需要权限，可以全部查看
}
