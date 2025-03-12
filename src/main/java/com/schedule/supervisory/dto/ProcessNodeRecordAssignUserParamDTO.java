package com.schedule.supervisory.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 流程节点记录-执行人
 */

@Data
public class ProcessNodeRecordAssignUserParamDTO implements Serializable {
    /**
     * 流程id (process id)
     */
    private String flowId;

    /**
     * 流程实例id (process instance id)
     */
    private String processInstanceId;

    /**
     * 表单数据 (form data)
     */
    private String data;

    /**
     * 本地数据 (local data)
     */
    private String localData;

    /**
     * 节点id (node id)
     */
    private String nodeId;

    /**
     * 用户id (user id)
     */
    private Long userId;

    /**
     * 节点id (node id)
     */
//    private NodeVo nextNode;

    /**
     * 任务状态 (task status，1-审核中 2-已完成 3-已驳回)
     */
    private Integer status;

    /**
     * 执行id (execution id)
     */
    private String executionId;

    /**
     * 任务id (task id)
     */
    private String taskId;

    /**
     * 审批描述 (approval description)
     */
    private String approveDesc;

    /**
     * 节点名称 (node name)
     */
    private String nodeName;

    /**
     * 任务类型 (task type)
     */
    private String taskType;
}
