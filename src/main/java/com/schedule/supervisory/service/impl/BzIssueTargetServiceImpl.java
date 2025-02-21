package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.BzIssueTargetMapper;
import com.schedule.supervisory.entity.BzIssueTarget;
import com.schedule.supervisory.service.IBzIssueTargetService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BzIssueTargetServiceImpl extends ServiceImpl<BzIssueTargetMapper, BzIssueTarget> implements IBzIssueTargetService {
    @Override
    public boolean updateProgressById(BzIssueTarget bzIssueTarget) {
        LambdaUpdateWrapper<BzIssueTarget> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BzIssueTarget::getId, bzIssueTarget.getId());
        if (bzIssueTarget.getBzIssueId() != null) {
            updateWrapper.set(BzIssueTarget::getBzIssueId, bzIssueTarget.getBzIssueId());
        }
        if (bzIssueTarget.getDept() != null) {
            updateWrapper.set(BzIssueTarget::getDept, bzIssueTarget.getDept());
        }
        if (bzIssueTarget.getDeptId() != null) {
            updateWrapper.set(BzIssueTarget::getDeptId, bzIssueTarget.getDeptId());
        }
        if (bzIssueTarget.getName() != null) {
            updateWrapper.set(BzIssueTarget::getName, bzIssueTarget.getName());
        }
        if (bzIssueTarget.getActualGear() != null) {
            updateWrapper.set(BzIssueTarget::getActualGear, bzIssueTarget.getActualGear());
        }
        if (bzIssueTarget.getPredictedGear() != null) {
            updateWrapper.set(BzIssueTarget::getPredictedGear, bzIssueTarget.getPredictedGear());
        }
        if (bzIssueTarget.getIssues() != null) {
            updateWrapper.set(BzIssueTarget::getIssues, bzIssueTarget.getIssues());
        }
        if (bzIssueTarget.getWorkProgress() != null) {
            updateWrapper.set(BzIssueTarget::getWorkProgress, bzIssueTarget.getWorkProgress());
        }
        return update(updateWrapper);
    }

    @Override
    public boolean updateProgress(BzIssueTarget bzIssueTarget) {
        LambdaUpdateWrapper<BzIssueTarget> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BzIssueTarget::getId, bzIssueTarget.getId());
        if (bzIssueTarget.getActualGear() != null) {
            updateWrapper.set(BzIssueTarget::getActualGear, bzIssueTarget.getActualGear());
        }
        if (bzIssueTarget.getPredictedGear() != null) {
            updateWrapper.set(BzIssueTarget::getPredictedGear, bzIssueTarget.getPredictedGear());
        }
        if (bzIssueTarget.getIssues() != null) {
            updateWrapper.set(BzIssueTarget::getIssues, bzIssueTarget.getIssues());
        }
        if (bzIssueTarget.getWorkProgress() != null) {
            updateWrapper.set(BzIssueTarget::getWorkProgress, bzIssueTarget.getWorkProgress());
        }
        return update(updateWrapper);
    }

    @Override
    public boolean reviewProgress(BzIssueTarget bzIssueTarget) {
        LambdaUpdateWrapper<BzIssueTarget> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BzIssueTarget::getId, bzIssueTarget.getId())
                .set(BzIssueTarget::getReviewerId, bzIssueTarget.getReviewerId())
                .set(BzIssueTarget::getReviewStatus, bzIssueTarget.getReviewStatus());
        return update(updateWrapper);
    }

    @Override
    public List<BzIssueTarget> getByIssueId(Long issueId) {
        LambdaQueryWrapper<BzIssueTarget> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BzIssueTarget::getBzIssueId, issueId)
                .orderByAsc(BzIssueTarget::getId);
        return list(queryWrapper);
    }
}