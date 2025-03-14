package com.schedule.supervisory.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.ProgressReportMapper;
import com.schedule.supervisory.dto.TaskWithProgressReportDTO;
import com.schedule.supervisory.entity.ProgressReport;
import com.schedule.supervisory.entity.Task;
import com.schedule.supervisory.service.IProgressReportService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ProgressReportServiceImpl extends ServiceImpl<ProgressReportMapper, ProgressReport> implements IProgressReportService {

    private ProgressReportMapper progressReportMapper;

    @Override
    public List<ProgressReport> getAllProgressReports() {
        return list();
    }

    @Override
    public ProgressReport getProgressReportById(Long id) {
        return getById(id);
    }

    @Override
    public ProgressReport createProgressReport(ProgressReport progressReport) {
        save(progressReport);
        return progressReport;
    }

    @Override
    public ProgressReport updateProgressReport(Long id, ProgressReport progressReportDetails) {
        ProgressReport progressReport = getProgressReportById(id);
        if (progressReport == null) {
            throw new RuntimeException("Progress Report not found for id: " + id);
        }
        if (progressReport.getTaskId() != null) {
            progressReport.setTaskId(progressReportDetails.getTaskId());
        }
        if (progressReport.getStageNodeId() != null) {
            progressReport.setStageNodeId(progressReportDetails.getStageNodeId());
        }
        if (progressReport.getStageNodeId() != null) {
            progressReport.setProgress(progressReportDetails.getProgress());
        }
        if (progressReport.getStageNodeId() != null) {
            progressReport.setIssuesAndChallenges(progressReportDetails.getIssuesAndChallenges());
        }
        if (progressReport.getStageNodeId() != null) {
            progressReport.setRequiresCoordination(progressReportDetails.getRequiresCoordination());
        }
        if (progressReport.getNextSteps() != null) {
            progressReport.setNextSteps(progressReportDetails.getNextSteps());
        }
        if (progressReport.getNextSteps() != null) {
            progressReport.setHandler(progressReportDetails.getHandler());
        }
        if (progressReport.getNextSteps() != null) {
            progressReport.setPhone(progressReportDetails.getPhone());
        }
        if (progressReport.getNextSteps() != null) {
            progressReport.setStatus(progressReportDetails.getStatus());
        }
        if (progressReport.getFlowId() != null) {
            progressReport.setFlowId(progressReportDetails.getFlowId());
        }
        if (progressReport.getProcessInstanceId() != null) {
            progressReport.setProcessInstanceId(progressReportDetails.getProcessInstanceId());
        }

        updateById(progressReport);
        return progressReport;
    }

    @Override
    public boolean updateProgressReportCheckInfo(ProgressReport progressReport) {
        LambdaUpdateWrapper<ProgressReport> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ProgressReport::getId, progressReport.getId());
        if (progressReport.getProcessInstanceId() != null) {
            updateWrapper.set(ProgressReport::getProcessInstanceId, progressReport.getProcessInstanceId());
        }
        if (progressReport.getFlowId() != null) {
            updateWrapper.set(ProgressReport::getFlowId, progressReport.getFlowId());
        }
        return update(updateWrapper);
    }

    @Override
    public void deleteProgressReport(Long id) {
        removeById(id);
    }

    @Override
    public List<ProgressReport> getProgressReportsByTaskId(Long taskId, String userId) {
//        return baseMapper.selectByTaskIdOrderByCreatedAtDesc(taskId);
        LambdaQueryWrapper<ProgressReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProgressReport::getTaskId, taskId); // 替换 yourTaskId 为实际的 task_id 值
        List<Integer> excludedStatuses = Arrays.asList(4, 5);
        if (userId != null) {
            queryWrapper.and(
                    wrapper -> wrapper.notIn(ProgressReport::getStatus, excludedStatuses)
                            .or(iw -> iw.in(ProgressReport::getStatus, excludedStatuses)
                                    .like(ProgressReport::getSubmitId, userId))
            );
        } else {
//            queryWrapper.ne(ProgressReport::getStatus, 4);
//            queryWrapper.ne(ProgressReport::getStatus, 5);
            queryWrapper.notIn(ProgressReport::getStatus, excludedStatuses);
        }
        queryWrapper.orderByDesc(ProgressReport::getId);

        return list(queryWrapper);
    }

    @Override
    public ProgressReport getNewProgressReportByTaskId(Long taskId) {
        LambdaQueryWrapper<ProgressReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProgressReport::getTaskId, taskId) // 替换 yourTaskId 为实际的 task_id 值
                .eq(ProgressReport::getStatus, 3) //不为3的
                .orderByDesc(ProgressReport::getId)
                .last("LIMIT 1");

        return getOne(queryWrapper);
    }

    @Override
    public List<ProgressReport> getProgressReportByTaskIds(List<Long> taskIds) {
        LambdaQueryWrapper<ProgressReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ProgressReport::getTaskId, taskIds) // 替换 yourTaskId 为实际的 task_id 值
//                .eq(ProgressReport::getStatus, 3) //不为3的
                .isNotNull(ProgressReport::getHandler)
                .isNotNull(ProgressReport::getPhone)
                .orderByDesc(ProgressReport::getId)
                .last("LIMIT 1");

        return list(queryWrapper);
    }

    @Override
    public boolean updateStatus(Integer id, Integer status) {
        LambdaUpdateWrapper<ProgressReport> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ProgressReport::getId, id)
                .set(ProgressReport::getStatus, status);
        return update(updateWrapper);
    }

    @Override
    public boolean revoke(Integer id, Integer status, String revokeDesc) {
        LambdaUpdateWrapper<ProgressReport> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ProgressReport::getId, id)
                .set(ProgressReport::getStatus, status)
                .set(ProgressReport::getRevokeDesc, revokeDesc);
        return update(updateWrapper);
    }

    @Override
    public List<TaskWithProgressReportDTO> checkFileCycle() {
        return progressReportMapper.findTasksWithConditions();
    }
}
