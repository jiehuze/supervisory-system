package com.schedule.supervisory.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDeptsDetail {
    private List<String> permissions;
    private List<String> roles;
    private SysUser sysUser;

}
