package com.schedule.supervisory.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReportUpdateDTO implements Serializable {
    private String leadingOfficial; //牵头领导
    private String leadingOfficialId; //牵头领导id
    private Long duchaReportId; //报告id
    private String reportFile; //报告文件地址
}
