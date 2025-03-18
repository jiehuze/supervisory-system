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
    private String operator;
    private String operatorId;
    private LocalDateTime createdAt;
    private String updatedBy;
    private String processInstanceId; //审核流水号
    //1：审核中；2：审核完成
    private Integer status;
}