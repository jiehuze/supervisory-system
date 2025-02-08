package com.schedule.supervisory.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DuchaReportCreationDTO implements Serializable {
    private List<Long> taskIds;
    private String submitter;
    private String submitterId;
    private String leadingOfficial; // 新字段名：报送领导（现称为leading_official）
    private String leadingOfficialId; // 新字段名：领导ID（现称为leading_official_id），使用字符串类型
}
