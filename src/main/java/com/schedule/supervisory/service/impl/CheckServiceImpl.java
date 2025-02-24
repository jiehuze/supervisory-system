package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.CheckMapper;
import com.schedule.supervisory.entity.Check;
import com.schedule.supervisory.service.ICheckService;
import org.springframework.stereotype.Service;

@Service
public class CheckServiceImpl extends ServiceImpl<CheckMapper, Check> implements ICheckService {
    @Override
    public boolean checkStatus(Check check) {
        LambdaUpdateWrapper<Check> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Check::getId, check.getId());
        updateWrapper.set(Check::getStatus, check.getStatus());
        return update(updateWrapper);
    }

    @Override
    public Check getByOnlyId(Check check) {
        LambdaQueryWrapper<Check> queryWrapper = new LambdaQueryWrapper<>();
        if (check.getId() != null) {
            queryWrapper.eq(Check::getId, check.getId()); // 查询没有删除
        }
        if (check.getTaskId() != null) {
            queryWrapper.eq(Check::getTaskId, check.getTaskId());
        }
        if (check.getStageId() != null) {
            queryWrapper.eq(Check::getStageId, check.getStageId());
        }
        if (check.getBzFormId() != null) {
            queryWrapper.eq(Check::getBzFormId, check.getBzFormId());
        }
        if (check.getBzIssueId() != null) {
            queryWrapper.eq(Check::getBzIssueId, check.getBzIssueId());
        }
        if (check.getBzFormTargetId() != null) {
            queryWrapper.eq(Check::getBzFormTargetId, check.getBzFormTargetId());
        }
        if (check.getBzIssueTargetId() != null) {
            queryWrapper.eq(Check::getBzIssueTargetId, check.getBzIssueTargetId());
        }
        if (check.getCheckType() != null) {
            queryWrapper.eq(Check::getCheckType, check.getCheckType());
        }
        queryWrapper.orderByDesc(Check::getId);
        queryWrapper.last("LIMIT 1");
        return getOne(queryWrapper);
    }
}