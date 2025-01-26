package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.entity.ProgressReport;

import java.util.List;

public interface IProgressReportService extends IService<ProgressReport> {
    List<ProgressReport> getAllProgressReports();

    ProgressReport getProgressReportById(Long id);

    ProgressReport createProgressReport(ProgressReport progressReport);

    ProgressReport updateProgressReport(Long id, ProgressReport progressReportDetails);

    void deleteProgressReport(Long id);

    List<ProgressReport> getProgressReportsByTaskId(Long taskId);

    boolean updateStatus(Integer id, Integer status);

    boolean revoke(Integer id, Integer status, String revokeDesc);
}