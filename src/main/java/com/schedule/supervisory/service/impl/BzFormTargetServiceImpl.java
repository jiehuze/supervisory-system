package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.BzFormTargetMapper;
import com.schedule.supervisory.dto.BzSearchDTO;
import com.schedule.supervisory.dto.DeptDTO;
import com.schedule.supervisory.entity.*;
import com.schedule.supervisory.service.IBzFormTargetService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BzFormTargetServiceImpl extends ServiceImpl<BzFormTargetMapper, BzFormTarget> implements IBzFormTargetService {
    @Override
    public boolean updateProgressById(BzFormTarget bzFormTarget) {
        LambdaUpdateWrapper<BzFormTarget> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BzFormTarget::getId, bzFormTarget.getId());
        if (bzFormTarget.getBzFormId() != null) {
            updateWrapper.set(BzFormTarget::getBzFormId, bzFormTarget.getBzFormId());
        }
        if (bzFormTarget.getDept() != null) {
            updateWrapper.set(BzFormTarget::getDept, bzFormTarget.getDept());
        }
        if (bzFormTarget.getDeptId() != null) {
            updateWrapper.set(BzFormTarget::getDeptId, bzFormTarget.getDeptId());
        }
        if (bzFormTarget.getName() != null) {
            updateWrapper.set(BzFormTarget::getName, bzFormTarget.getName());
        }
        if (bzFormTarget.getActualGear() != null) {
            updateWrapper.set(BzFormTarget::getActualGear, bzFormTarget.getActualGear());
        }
        if (bzFormTarget.getPredictedGear() != null) {
            updateWrapper.set(BzFormTarget::getPredictedGear, bzFormTarget.getPredictedGear());
        }
        if (bzFormTarget.getIssues() != null) {
            updateWrapper.set(BzFormTarget::getIssues, bzFormTarget.getIssues());
        }
        if (bzFormTarget.getWorkProgress() != null) {
            updateWrapper.set(BzFormTarget::getWorkProgress, bzFormTarget.getWorkProgress());
        }
        return update(updateWrapper);
    }

    @Override
    public boolean updateProgress(BzFormTarget bzFormTarget) {
        LambdaUpdateWrapper<BzFormTarget> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BzFormTarget::getId, bzFormTarget.getId());
        if (bzFormTarget.getActualGear() != null) {
            updateWrapper.set(BzFormTarget::getActualGear, bzFormTarget.getActualGear());
        }
        if (bzFormTarget.getPredictedGear() != null) {
            updateWrapper.set(BzFormTarget::getPredictedGear, bzFormTarget.getPredictedGear());
        }
        if (bzFormTarget.getIssues() != null) {
            updateWrapper.set(BzFormTarget::getIssues, bzFormTarget.getIssues());
        }
        if (bzFormTarget.getWorkProgress() != null) {
            updateWrapper.set(BzFormTarget::getWorkProgress, bzFormTarget.getWorkProgress());
        }
        return update(updateWrapper);
    }

    @Override
    public boolean reviewProgress(BzFormTarget bzFormTarget) {
        LambdaUpdateWrapper<BzFormTarget> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BzFormTarget::getId, bzFormTarget.getId())
                .set(BzFormTarget::getReviewerId, bzFormTarget.getReviewerId())
                .set(BzFormTarget::getReviewStatus, bzFormTarget.getReviewStatus());
        return update(updateWrapper);
    }

    @Override
    public boolean updateCheckById(Long id, Integer addStatus, Integer removeStatus) {
        LambdaUpdateWrapper<BzFormTarget> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BzFormTarget::getId, id);
        if (addStatus != null) {
            updateWrapper.set(BzFormTarget::getCheckStatus, addStatus);
        }
        if (removeStatus != null) {
            updateWrapper.set(BzFormTarget::getCheckStatus, 0);
        }

        return update(updateWrapper);
    }

    @Override
    public List<BzFormTarget> getByFormId(BzSearchDTO bzSearchDTO, List<DeptDTO> deptDTOs) {
        LambdaQueryWrapper<BzFormTarget> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BzFormTarget::getBzFormId, bzSearchDTO.getBzFormId());

        if (bzSearchDTO.getCheckStatus() != null && !bzSearchDTO.getCheckStatus().isEmpty()) {
            queryWrapper.like(BzFormTarget::getCheckStatus, bzSearchDTO.getCheckStatus());
        }

        if ((bzSearchDTO.getUserId() != null && !bzSearchDTO.getUserId().isEmpty())
                || (deptDTOs != null && deptDTOs.size() > 0)) {
            // 处理leadingOfficialId模糊查询的情况
            queryWrapper.and(wrapper -> {
                if (bzSearchDTO.getUserId() != null && !bzSearchDTO.getUserId().isEmpty()) {
                    wrapper.or(w -> w.like(BzFormTarget::getAssignerId, bzSearchDTO.getUserId()));
                }

                if (deptDTOs != null && deptDTOs.size() > 0) {
                    for (DeptDTO deptDTO : deptDTOs) {
                        wrapper.or(w -> w.like(BzFormTarget::getDeptId, deptDTO.getDeptId()));
                        wrapper.or(w -> w.like(BzFormTarget::getLeadingDepartmentId, deptDTO.getDeptId()));
                    }
                }
            });
        }

        queryWrapper.orderByAsc(BzFormTarget::getId);
        return list(queryWrapper);
    }
}