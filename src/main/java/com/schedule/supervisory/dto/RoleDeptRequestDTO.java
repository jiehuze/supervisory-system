package com.schedule.supervisory.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RoleDeptRequestDTO implements Serializable {
    private List<String> roleCodes;
    private List<String> deptIds;
}
