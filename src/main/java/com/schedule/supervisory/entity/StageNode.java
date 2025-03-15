package com.schedule.supervisory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("stage_node")
public class StageNode {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer taskId;

    private String stageGoal;

    private LocalDate deadline;

    /**
     * 1: 正常推进
     * 2： 完成
     * 3： 已逾期 （废除，使用OverdueDays记录逾期）
     * 4: 审核中（办结申请，承办领导审批）
     */
    private Integer status;
    private Integer overdueDays; // 新增逾期天数字段，当状态为3时，计算超期天数
    private String processInstanceId; //审核流水号

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}