package com.schedule.supervisory.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("bz_issue_target")
public class BzIssueTarget {
    @TableId
    private Long id;
    private Long bzIssueId;
    private String name;
    private String dept;
    private String deptId;
    private Integer predictedGear;
    private Integer actualGear;
    private String workProgress;
    private String issues;
    // 审核状态：1：通过；2：退回
    private Integer reviewStatus;
    private String reviewerId;
    private String operator;
    private String operatorId;
    private String assigner; //交办人
    private String assignerId; //交办人Id
    private String checkStatus; //审核状态 1: 任务审核；2：阶段性审核；3：报表审核；4：指标审核，用逗号分割
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}