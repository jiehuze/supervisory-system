package com.schedule.supervisory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("bz_form_target")
public class BzFormTarget {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer bzFormId;
    private String name;
    private String dept;
    private String deptId;
    private Integer predictedGear;
    private Integer actualGear;
    private String workProgress;
    private String issues;
    // 审核状态：0：提交；1：通过；2：退回；
    private Integer reviewStatus;
    private String reviewerId;
    private boolean delete;
    private String operator;
    private String operatorId;
    private String assigner; //交办人
    private String assignerId; //交办人Id
    private String checkStatus; //审核状态 1: 任务审核；2：阶段性审核；3：报表审核；4：指标审核，用逗号分割
    private String leadingDepartment; // 牵头单位 'leading_department'
    private String leadingDepartmentId; // 牵头单位ID 'leading_department_id'
    private String processInstanceId; //审核流水号
    private String processInstanceReviewIds; //审核的人，使用逗号分割
    private String gearDesc; //档位描述
    private String majorRuleChange; //重大规则变动
    private String attachment; //附件
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}