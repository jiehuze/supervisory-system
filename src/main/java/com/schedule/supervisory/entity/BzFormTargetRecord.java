package com.schedule.supervisory.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("bz_form_target_record")
public class BzFormTargetRecord {
    @TableId
    private Long id;
    private Long targetId;
    private String workProgress;
    private String issue;
    private LocalDateTime createdAt;
    private String updatedBy;
}