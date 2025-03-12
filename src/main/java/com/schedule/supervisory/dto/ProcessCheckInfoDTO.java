package com.schedule.supervisory.dto;

import lombok.Data;

/**
 * 需要记录在数据库中的流程引擎的数据
 * 包括：1： 流水号；2：要审核的审核人；
 */
@Data
public class ProcessCheckInfoDTO {
    private String processInstanceId;
    private String reviewIds;
}
