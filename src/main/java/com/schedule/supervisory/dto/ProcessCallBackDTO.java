package com.schedule.supervisory.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProcessCallBackDTO implements Serializable {
    private String processInstanceId;
    private String flowId;
    private String nodeId;
    private String userId;
    private String taskId;
    private Integer status;

}
