package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.StageNodeMapper;
import com.schedule.supervisory.entity.StageNode;
import com.schedule.supervisory.service.IStageNodeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StageNodeServiceImpl extends ServiceImpl<StageNodeMapper, StageNode> implements IStageNodeService {

    @Override
    public List<StageNode> getStageNodesByTaskId(Integer taskId) {
        return baseMapper.selectByTaskIdOrderByCreatedAtDesc(taskId);
    }

    @Override
    public StageNode createStageNode(StageNode stageNode) {
        save(stageNode); // 使用 MyBatis-Plus 的 save 方法保存实体
        return stageNode;
    }

    @Override
    public void batchCreateStageNodes(List<StageNode> stageNodes) {
        saveBatch(stageNodes);
    }
}