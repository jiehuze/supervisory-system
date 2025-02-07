package com.schedule.supervisory.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class TaskStatistics implements Serializable {
    private Long totals;
    private Long inprogressNums;
    private Long overdueNums;
    private Long completeNums;
    private Long completOnTimesNums;
}
