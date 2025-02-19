package com.schedule.supervisory.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.ProgressReportMapper;
import com.schedule.supervisory.dto.TaskWithProgressReportDTO;
import com.schedule.supervisory.entity.ProgressReport;
import com.schedule.supervisory.service.IProgressReportService;
import org.springframework.stereotype.Service;

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
        progressReport.setTaskId(progressReportDetails.getTaskId());
        progressReport.setStageNodeId(progressReportDetails.getStageNodeId());
        progressReport.setProgress(progressReportDetails.getProgress());
        progressReport.setIssuesAndChallenges(progressReportDetails.getIssuesAndChallenges());
        progressReport.setRequiresCoordination(progressReportDetails.getRequiresCoordination());
        progressReport.setNextSteps(progressReportDetails.getNextSteps());
        progressReport.setHandler(progressReportDetails.getHandler());
        progressReport.setPhone(progressReportDetails.getPhone());
        progressReport.setStatus(progressReportDetails.getStatus());

        updateById(progressReport);
        return progressReport;
    }

    @Override
    public void deleteProgressReport(Long id) {
        removeById(id);
    }

    @Override
    public List<ProgressReport> getProgressReportsByTaskId(Long taskId) {
        return baseMapper.selectByTaskIdOrderByCreatedAtDesc(taskId);
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
