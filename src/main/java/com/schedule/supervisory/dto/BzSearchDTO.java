package com.schedule.supervisory.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class BzSearchDTO implements Serializable {
    private Long id; // 表id
    private Long bzFormId;
    private Long bzIssuedId;
    private Long bzFormTargetId;
    private Long bzIssueTargetId;
    private String name; // 名称
    private String type; // 类型
    private Integer typeId; // 类型id
    private Integer actualGear; // 档位
    private Integer predictedGear; //预估档位
    private LocalDateTime createdAtStart;
    private LocalDateTime createdAtEnd;
    //时间类型：1：年；2：季度
    private Integer dateType; // 新增的日期类型字段
    private Integer year; // 年份
    private Integer quarter; // 季度
    private String userId;
    private String checkStatus;
}
