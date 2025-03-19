package com.schedule.supervisory.service;

import com.schedule.supervisory.entity.StageNode;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IStageNodeService extends IService<StageNode> {
    List<StageNode> getStageNodesByTaskId(Integer taskId);

    List<StageNode> getStageNodeForOverdue(Long taskId);

    StageNode createStageNode(StageNode stageNode);

    boolean batchCreateStageNodes(List<StageNode> stageNodes);

    boolean updateStatusById(Integer id, Integer status);

    void updateCheckProcess(Long id, String processInstanceId, String processInstanceReviewIds);

    //更新超期任务
    void updateOverdueDays();
}