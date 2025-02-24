package com.schedule.supervisory.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("bz_issue_target_record")
public class BzIssueTargetRecord {
    @TableId
    private Long id;
    private Long targetId;
    private String workProgress;
    private String issue;
    private String operator;
    private String operatorId;
    private LocalDateTime createdAt;
    private String updatedBy;
}