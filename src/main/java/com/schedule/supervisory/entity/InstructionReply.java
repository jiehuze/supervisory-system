package com.schedule.supervisory.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("instruction_reply") // 指定表名
public class InstructionReply {

    @TableId
    private Integer id; // 主键

    private Integer instructionId; // 批示 ID

    private String replyContent; // 回复内容

    private String operator; // 操作人

    private String operatorId; // 操作人 ID

    private LocalDateTime createdAt; // 创建时间
}