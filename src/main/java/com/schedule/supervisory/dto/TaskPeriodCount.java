package com.schedule.supervisory.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TaskPeriodCount implements Serializable {
    public TaskPeriodCount() {
    }

    public TaskPeriodCount(int total, int complete, int period, String periodName) {
        this.total = total;
        this.complete = complete;
        this.period = period;
        this.periodName = periodName;
    }

    private int total;
    private int complete;
    private int period;
    private String periodName;
}
