package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.dto.DuchaReportCreationDTO;
import com.schedule.supervisory.dto.ReportUpdateDTO;
import com.schedule.supervisory.entity.DuchaReport;

public interface IDuchaReportService extends IService<DuchaReport> {
    Page<DuchaReport> searchReports(String submitterId, String leadingOfficialId, Integer pageNum, Integer pageSize);

    boolean generateReportFromTasks(DuchaReportCreationDTO duchaReportCreationDTO);

    boolean updateSubmitById(Long duchaReportId, ReportUpdateDTO reportSubmissionDTO);

    boolean updateReportFileById(Long duchaReportId, ReportUpdateDTO reportSubmissionDTO);

    boolean deleteById(Long duchaReportId);
}