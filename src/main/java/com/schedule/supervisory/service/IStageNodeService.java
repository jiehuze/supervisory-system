package com.schedule.supervisory.service;

import com.schedule.supervisory.entity.StageNode;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface IStageNodeService extends IService<StageNode> {
    List<StageNode> getStageNodesByTaskId(Integer taskId);
    StageNode createStageNode(StageNode stageNode);
    boolean batchCreateStageNodes(List<StageNode> stageNodes);
    boolean updateStatusById(Integer id, Integer status);
}