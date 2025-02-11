package com.schedule.supervisory.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class BzFormStatistics implements Serializable {
    private Long bzFromCount;
    private Long bzIssueCount;
    private Long bzFormTargetCount;
    private Long bzIssueTargetCount;
    private Map<Integer, CountDTO> bzFormGears;
    private Map<Integer, CountDTO> bzIssueGears;
}
