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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}