package com.schedule.supervisory.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class CheckStartDTO implements Serializable {
    private String flowId;
    private Map<String, String> paramMap;
}
