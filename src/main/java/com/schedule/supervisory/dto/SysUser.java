package com.schedule.supervisory.dto;

import lombok.Data;

import java.util.List;

@Data
public class SysUser {
    private String passwordModifyTime;
    private String passwordExpireFlag;
    private String delFlag;
    private String updateBy;
    private List<String> deptIds;
    private String deptId;
    private String updateTime;
    private String userId;
    private String lockFlag;
    private String createBy;
    private String createTime;
    private String phone;
    private String tenantId;
    private String name;
    private String username;
}
