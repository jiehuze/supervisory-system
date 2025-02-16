package com.schedule.supervisory.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserDataDTO implements Serializable {
    private String userId;
    private String username;
    private String wxOpenid;
    private String qqOpenid;
    private String giteeOpenId;
    private String oscOpenId;
    private String wxCpUserid;
    private String wxDingUserid;
//    private Date createTime;
//    private Date updateTime;
    private String delFlag;
    private String orderNo;
    private String lockFlag;
    private String passwordExpireFlag;
    private Date passwordModifyTime;
    private String phone;
    private String avatar;
    private String deptId;
    private String tenantId;
    private String deptName;
    //    private List<?> roleList; // 更具具体化类型，如果已知的话
//    private List<?> postList; // 更具具体化类型，如果已知的话
    private String nickname;
    private String name;
    private String email;
}
