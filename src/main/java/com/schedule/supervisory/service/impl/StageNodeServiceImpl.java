package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.StageNodeMapper;
import com.schedule.supervisory.entity.StageNode;
import com.schedule.supervisory.service.IStageNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StageNodeServiceImpl extends ServiceImpl<StageNodeMapper, StageNode> implements IStageNodeService {
    @Autowired
    private StageNodeMapper stageNodeMapper;

    @Override
    public List<StageNode> getStageNodesByTaskId(Integer taskId) {
        return baseMapper.selectByTaskIdOrderByCreatedAtDesc(taskId);
    }

    @Override
    public List<StageNode> getStageNodeForOverdue(Long taskId) {
        LambdaQueryWrapper<StageNode> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StageNode::getTaskId, taskId);
        queryWrapper.ne(StageNode::getStatus, 2);
        queryWrapper.gt(StageNode::getOverdueDays, 0);

        return list(queryWrapper);
    }

    @Override
    public StageNode createStageNode(StageNode stageNode) {
        save(stageNode); // 使用 MyBatis-Plus 的 save 方法保存实体
        return stageNode;
    }

    @Override
    public boolean batchCreateStageNodes(List<StageNode> stageNodes) {
        return saveBatch(stageNodes);
    }

    @Override
    public boolean updateStatusById(Integer id, Integer status) {
        // 验证状态值是否在允许的范围内 (0 到 5)
        if (status < 0 || status > 5) {
            return false;
        }

        // 构建更新条件
        LambdaUpdateWrapper<StageNode> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(StageNode::getId, id)
                .set(StageNode::getStatus, status);

        // 执行更新操作并返回是否成功
        return update(updateWrapper);
    }

    @Override
    public void updateCheckProcess(Long id, String processInstanceId, String processInstanceReviewIds, String cbDoneDesc, String cbDoneFile) {
        LambdaUpdateWrapper<StageNode> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(StageNode::getId, id);
        if (processInstanceId != null) {
            updateWrapper.set(StageNode::getProcessInstanceId, processInstanceId);
        }
        if (cbDoneDesc != null) {
            updateWrapper.set(StageNode::getCbDoneDesc, cbDoneDesc);
        }
        if (cbDoneFile != null) {
            updateWrapper.set(StageNode::getCbDoneFile, cbDoneFile);
        }

        // 执行更新操作并返回是否成功
        update(updateWrapper);
    }

    @Override
    public void updateOverdueDays() {
        stageNodeMapper.updateOverdueDays();
    }
}