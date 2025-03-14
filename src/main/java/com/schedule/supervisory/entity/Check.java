package com.schedule.supervisory.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("duban_check")
public class Check extends Model<Check> {
    @TableId
    private Long id;

    private Long taskId;
    private Long stageId;
    private Long bzFormId;
    private Long bzIssueId;
    private Long bzFormTargetId;
    private Long bzIssueTargetId;
    private String dataJson;
    //1： 审核中；2：通过审核，3.驳回
    private Integer status;
    /**
     * 1. 任务进度提交审核；
     * 2：任务阶段性目标提交审核；
     * 3：:885清单列表详情修改提交审核；
     * 4：:885清单列表指标修改提交审核；
     * 5：:885问题列表详情修改提交审核；
     * 6：:885问题列表指标修改提交审核
     * 7：任务办结提交审核
     * 8：任务终结提交审核
     */
    private Integer checkType;
    private String operator;
    private String operatorId;
    private String processInstanceId;
    private String flowId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}