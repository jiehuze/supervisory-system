package com.schedule.supervisory.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskSearchDTO implements Serializable {
    private Long taskId;
    private Integer taskType; //0:督查室；1：个人
    private String source; // 任务来源 'source'
    private String content; // 任务内容 'content'
    private String leadingOfficial; // 牵头区领导 'leading_official'
    private String leadingOfficialId; // 牵头区领导id
    private Boolean leadingPerson; //是否是牵头人
    private String leadingDepartment; // 牵头单位 'leading_department'
    private String leadingDepartmentId; // 牵头单位ID 'leading_department_id'
    private LocalDate deadline; // 完成时间 'deadline'
    private Integer fieldId; // 所属领域（int类型）对应数据库列 'field_id'
    private Integer status; // 任务状态 'status'
    private Integer overdueDays; // 新增逾期天数字段，当状态为3时，计算超期天数
    private LocalDate sourceDate; // 来源时间（到天）对应数据库列 'source_date'
    private String responsiblePerson; // 责任人对应数据库列 'responsible_person'
    private String responsiblePersonId; // 责任人ID 'responsible_person_id'
    private String assignerId; //交办人Id
    private Integer taskPeriod; // 任务周期（int类型）对应数据库列 'task_period'

    private String userId;
    private String coOrganizerId;
    private LocalDateTime createdAtStart;
    private LocalDateTime createdAtEnd;
    private Boolean unfinished; //未完成的
    private Boolean untreated; //待处理的
    private Boolean accept;
    private Boolean unAuth; //不需要权限，可以全部查看
    private Boolean phoneUsed; //手机统计需要特殊处理
    private Boolean deleteField;
    private String taskIds;
    private List<Long> taskIdList;
}
