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
    public boolean updateProgress(BzIssueTarget bzIssueTarget) {
        LambdaUpdateWrapper<BzIssueTarget> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BzIssueTarget::getId, bzIssueTarget.getId())
                .set(BzIssueTarget::getActualGear, bzIssueTarget.getActualGear())
                .set(BzIssueTarget::getPredictedGear, bzIssueTarget.getPredictedGear())
                .set(BzIssueTarget::getIssues, bzIssueTarget.getIssues())
                .set(BzIssueTarget::getWorkProgress, bzIssueTarget.getWorkProgress());
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
        queryWrapper.eq(BzIssueTarget::getBzIssueId, issueId);
        return list(queryWrapper);
    }
}