package com.schedule.supervisory.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("public.check")
public class Check extends Model<Check> {
    @TableId
    private Integer id;

    private Integer taskId;
    private Integer stageId;
    private Integer bzFormId;
    private Integer bzIssueId;
    private Integer bzFormTargetId;
    private Integer bzIssueTargetId;
    private String dataJson;
    //1： 审核中；2：通过审核，3.未通过审核
    private Integer status;
    private String operator;
    private String operatorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}