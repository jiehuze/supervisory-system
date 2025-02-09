package com.schedule.supervisory.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeptDTO implements Serializable {
    private String deptId;
    private String name;
    private int sortOrder;
}
