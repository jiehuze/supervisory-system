package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.dto.TaskWithProgressReportDTO;
import com.schedule.supervisory.entity.ProgressReport;

import java.util.List;

public interface IProgressReportService extends IService<ProgressReport> {
    List<ProgressReport> getAllProgressReports();

    ProgressReport getProgressReportById(Long id);

    ProgressReport createProgressReport(ProgressReport progressReport);

    ProgressReport updateProgressReport(Long id, ProgressReport progressReportDetails);

    boolean updateProgressReportCheckInfo(ProgressReport progressReport);

    void deleteProgressReport(Long id);

    List<ProgressReport> getProgressReportsByTaskId(Long taskId, String userId);

    ProgressReport getNewProgressReportByTaskId(Long taskId);

    List<ProgressReport> getProgressReportByTaskIds(List<Long> taskIds);

    boolean updateStatus(Integer id, Integer status);

    boolean revoke(Integer id, Integer status, String revokeDesc);

    List<TaskWithProgressReportDTO> checkFileCycle();
}