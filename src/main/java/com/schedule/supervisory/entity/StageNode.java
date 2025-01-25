package com.schedule.supervisory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("stage_node")
public class StageNode {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer taskId;

    private String stageGoal;

    private LocalDateTime deadline;

    /**
     * 1: 正常推进
     * 2： 完成
     * 3： 已逾期
     */
    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}