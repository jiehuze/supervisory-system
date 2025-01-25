package com.schedule.supervisory.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TaskCollectDTO implements Serializable {
    // 总任务数
    private Integer totalTasks;

    // 推进中的任务数
    private Integer progressingTasks;

    // 逾期任务数
    private Integer overdueTasks;

    // 办结任务数
    private Integer completedTasks;

    // 总办结率
    private Integer completionRate;

    // 短期办结率
    private Integer shortTermCompletionRate;
}
