package com.schedule.supervisory.controller;

import com.schedule.common.BaseResponse;
import com.schedule.supervisory.entity.ProgressReport;
import com.schedule.supervisory.service.IProgressReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public BaseResponse getProgressReportById(@PathVariable(value = "id") Long progressReportId) {
        ProgressReport progressReport = progressReportService.getProgressReportById(progressReportId);

        return new BaseResponse(HttpStatus.OK.value(), "success", progressReport, Integer.toString(0));
    }

    @PostMapping
    public BaseResponse createProgressReport(@RequestBody ProgressReport progressReport) {
        ProgressReport progressReport1 = progressReportService.createProgressReport(progressReport);

        return new BaseResponse(HttpStatus.OK.value(), "success", progressReport1, Integer.toString(0));
    }

    @PutMapping("/{id}")
    public BaseResponse updateProgressReport(@PathVariable(value = "id") Long progressReportId,
                                             @RequestBody ProgressReport progressReportDetails) {
        ProgressReport progressReport = progressReportService.updateProgressReport(progressReportId, progressReportDetails);
        return new BaseResponse(HttpStatus.OK.value(), "success", progressReport, Integer.toString(0));
    }

//    @DeleteMapping("/{id}")
//    public void deleteProgressReport(@PathVariable(value = "id") Long progressReportId) {
//        progressReportService.deleteProgressReport(progressReportId);
//    }

    @GetMapping("/task/{taskId}")
    public BaseResponse getProgressReportsByTaskId(@PathVariable(value = "taskId") Long taskId) {
        List<ProgressReport> progressReports = progressReportService.getProgressReportsByTaskId(taskId);
        return new BaseResponse(HttpStatus.OK.value(), "success", progressReports, Integer.toString(0));
    }
}
