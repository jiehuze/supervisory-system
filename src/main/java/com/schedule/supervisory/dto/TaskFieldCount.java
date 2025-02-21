package com.schedule.supervisory.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TaskFieldCount implements Serializable {
    private int total;
    private int complete;
    private Long fieldId;
    private String fieldName;
}
