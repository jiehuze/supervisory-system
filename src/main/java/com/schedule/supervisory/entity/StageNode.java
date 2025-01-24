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

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}