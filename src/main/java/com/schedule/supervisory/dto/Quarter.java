package com.schedule.supervisory.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Quarter {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String name; // 第几季度
    private int quarterNumber; // 记录第几季度这个数

    public Quarter(LocalDateTime startTime, LocalDateTime endTime, String name, int quarterNumber) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
        this.quarterNumber = quarterNumber;
    }
}
