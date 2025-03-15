package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.BzIssueTargetMapper;
import com.schedule.supervisory.dto.BzSearchDTO;
import com.schedule.supervisory.dto.DeptDTO;
import com.schedule.supervisory.entity.BzFormTarget;
import com.schedule.supervisory.entity.BzIssueTarget;
import com.schedule.supervisory.entity.Task;
import com.schedule.supervisory.service.IBzIssueTargetService;
import com.schedule.utils.util;
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
    public boolean updateCheckById(Long id, Integer addStatus, Integer removeStatus) {
        BzIssueTarget bzIssueTarget = getById(id);
        String checkStatus = bzIssueTarget.getCheckStatus();
        if (addStatus != null) {
            checkStatus = util.joinString(checkStatus, addStatus.toString());
        }
        if (removeStatus != null) {
            checkStatus = util.removeString(checkStatus, removeStatus.toString());
        }

        LambdaUpdateWrapper<BzIssueTarget> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BzIssueTarget::getId, id)
                .set(BzIssueTarget::getCheckStatus, checkStatus);

        return update(updateWrapper);
    }

    @Override
    public void updateCheckProcess(Long id, String processInstanceId, String processInstanceReviewIds) {
        LambdaUpdateWrapper<BzIssueTarget> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BzIssueTarget::getId, id)
                .set(BzIssueTarget::getProcessInstanceId, processInstanceId)
                .set(BzIssueTarget::getProcessInstanceReviewIds, processInstanceReviewIds);

        update(updateWrapper);
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
    public List<BzIssueTarget> getByIssueId(BzSearchDTO bzSearchDTO, List<DeptDTO> deptDTOs) {
        LambdaQueryWrapper<BzIssueTarget> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BzIssueTarget::getBzIssueId, bzSearchDTO.getBzIssueId());
        if (bzSearchDTO.getCheckStatus() != null && !bzSearchDTO.getCheckStatus().isEmpty()) {
            queryWrapper.like(BzIssueTarget::getCheckStatus, bzSearchDTO.getCheckStatus());
        }
        queryWrapper.eq(BzIssueTarget::isDelete, false);//没有删除的
        if ((bzSearchDTO.getUserId() != null && !bzSearchDTO.getUserId().isEmpty())
                || (deptDTOs != null && deptDTOs.size() > 0)) {
            queryWrapper.and(wrapper -> {
                if (bzSearchDTO.getUserId() != null && !bzSearchDTO.getUserId().isEmpty()) {
                    wrapper.or(w -> w.like(BzIssueTarget::getAssignerId, bzSearchDTO.getUserId()));
                }

                if (deptDTOs != null && deptDTOs.size() > 0) {
                    for (DeptDTO deptDTO : deptDTOs) {
                        wrapper.or(w -> w.like(BzIssueTarget::getDeptId, deptDTO.getDeptId()));
                        wrapper.or(w -> w.like(BzIssueTarget::getLeadingDepartmentId, deptDTO.getDeptId()));
                    }
                }
            });
        }
        queryWrapper.orderByAsc(BzIssueTarget::getId);
        return list(queryWrapper);
    }

    @Override
    public List<BzIssueTarget> getCheckByIssueId(BzSearchDTO bzSearchDTO, List<DeptDTO> deptDTOs) {
        LambdaQueryWrapper<BzIssueTarget> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BzIssueTarget::getBzIssueId, bzSearchDTO.getBzIssueId());
        if (bzSearchDTO.getCheckStatus() != null && !bzSearchDTO.getCheckStatus().isEmpty()) {
            queryWrapper.like(BzIssueTarget::getCheckStatus, bzSearchDTO.getCheckStatus());
        }
        queryWrapper.eq(BzIssueTarget::isDelete, false);//没有删除的

        if ((bzSearchDTO.getUserId() != null && !bzSearchDTO.getUserId().isEmpty())
                || (deptDTOs != null && deptDTOs.size() > 0)) {
            queryWrapper.and(wrapper -> {
                if (bzSearchDTO.getUserId() != null && !bzSearchDTO.getUserId().isEmpty()) {
                    wrapper.or(w -> w.like(BzIssueTarget::getAssignerId, bzSearchDTO.getUserId()));
                }

                if (deptDTOs != null && deptDTOs.size() > 0) {
                    for (DeptDTO deptDTO : deptDTOs) {
                        wrapper.or(w -> w.like(BzIssueTarget::getDeptId, deptDTO.getDeptId()));
                        wrapper.or(w -> w.like(BzIssueTarget::getLeadingDepartmentId, deptDTO.getDeptId()));
                    }
                }
            });
        }
        queryWrapper.orderByAsc(BzIssueTarget::getId);
        return list(queryWrapper);
    }
}