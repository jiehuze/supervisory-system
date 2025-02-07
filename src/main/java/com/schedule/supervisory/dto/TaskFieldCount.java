package com.schedule.supervisory.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class TaskFieldCount implements Serializable {
    private int total;
    private int complete;
    private int fieldId;
    private String fieldName;
}
