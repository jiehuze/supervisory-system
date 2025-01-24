package com.schedule.supervisory.controller;

import com.schedule.supervisory.entity.ProgressReport;
import com.schedule.supervisory.service.IProgressReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/progress-reports")
public class ProgressReportController {

    @Autowired
    private IProgressReportService progressReportService;

    @GetMapping
    public List<ProgressReport> getAllProgressReports() {
        return progressReportService.getAllProgressReports();
    }

    @GetMapping("/{id}")
    public ProgressReport getProgressReportById(@PathVariable(value = "id") Long progressReportId) {
        return progressReportService.getProgressReportById(progressReportId);
    }

    @PostMapping
    public ProgressReport createProgressReport(@RequestBody ProgressReport progressReport) {
        return progressReportService.createProgressReport(progressReport);
    }

    @PutMapping("/{id}")
    public ProgressReport updateProgressReport(@PathVariable(value = "id") Long progressReportId,
                                               @RequestBody ProgressReport progressReportDetails) {
        return progressReportService.updateProgressReport(progressReportId, progressReportDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteProgressReport(@PathVariable(value = "id") Long progressReportId) {
        progressReportService.deleteProgressReport(progressReportId);
    }

    @GetMapping("/task/{taskId}")
    public List<ProgressReport> getProgressReportsByTaskId(@PathVariable(value = "taskId") Long taskId) {
        return progressReportService.getProgressReportsByTaskId(taskId);
    }
}
