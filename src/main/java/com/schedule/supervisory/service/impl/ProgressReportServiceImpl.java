package com.schedule.supervisory.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.ProgressReportMapper;
import com.schedule.supervisory.entity.ProgressReport;
import com.schedule.supervisory.service.IProgressReportService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgressReportServiceImpl extends ServiceImpl<ProgressReportMapper, ProgressReport> implements IProgressReportService {

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
        progressReport.setProgress(progressReportDetails.getProgress());
        progressReport.setIssuesAndChallenges(progressReportDetails.getIssuesAndChallenges());
        progressReport.setRequiresCoordination(progressReportDetails.getRequiresCoordination());
        progressReport.setNextSteps(progressReportDetails.getNextSteps());
        progressReport.setHandler(progressReportDetails.getHandler());
        progressReport.setPhone(progressReportDetails.getPhone());

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
}
