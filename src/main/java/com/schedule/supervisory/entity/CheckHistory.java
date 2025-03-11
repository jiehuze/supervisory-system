package com.schedule.supervisory.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("check_history")
public class CheckHistory {
    @TableId
    private Long id;
    private Integer checkId;
    private String reviewer;
    private Integer reviewerId;
    private String role;
    private String roleCode;
    private Integer reviewStatus;
    private String reviewComment;
    private LocalDateTime reviewAt;
}