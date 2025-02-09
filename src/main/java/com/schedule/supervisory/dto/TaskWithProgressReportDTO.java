package com.schedule.supervisory.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class TaskWithProgressReportDTO implements Serializable {
    private Long taskId;
    private LocalDate lastProgressCreatedAt;
    private Integer daysDiff;
    private Integer fillCycle;
    private Integer status;
    private String handlerId;
    private String responsiblePersonId;
    private String source; // 任务来源 'source'
    private LocalDate sourceDate; // 来源时间（到天）对应数据库列 'source_date'
}
