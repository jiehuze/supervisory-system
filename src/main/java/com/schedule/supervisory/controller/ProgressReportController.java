package com.schedule.supervisory.controller;

import com.schedule.common.BaseResponse;
import com.schedule.supervisory.dto.BzSearchDTO;
import com.schedule.supervisory.entity.ProgressReport;
import com.schedule.supervisory.entity.Task;
import com.schedule.supervisory.service.IProgressReportService;
import com.schedule.supervisory.service.ITaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/progress-reports")
public class ProgressReportController {

    @Autowired
    private IProgressReportService progressReportService;

    @Autowired
    private ITaskService taskService;

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

    //提交审核
    @PostMapping("/check")
    public BaseResponse createCheckProgressReport(@RequestBody ProgressReport progressReport) {
        progressReport.setStatus(4); //设置为审核状态
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
    public BaseResponse getProgressReportsByTaskId(@PathVariable(value = "taskId") Long taskId, @ModelAttribute BzSearchDTO bzSearchDTO) {
        List<ProgressReport> progressReports = progressReportService.getProgressReportsByTaskId(taskId, bzSearchDTO.getUserId());
        return new BaseResponse(HttpStatus.OK.value(), "success", progressReports, Integer.toString(0));
    }

    @PutMapping("/{id}/status")
    public BaseResponse updateStatus(@PathVariable Integer id, @RequestParam Integer status) {
        boolean update = progressReportService.updateStatus(id, status);
        return new BaseResponse(HttpStatus.OK.value(), "success", update, Integer.toString(0));
    }

    @PutMapping("/{id}/revoke")
    public BaseResponse revoke(@PathVariable Integer id, @RequestBody ProgressReport progressReport) {
        boolean update = progressReportService.revoke(id, progressReport.getStatus(), progressReport.getRevokeDesc());

        ProgressReport report = progressReportService.getById(id);
        if (report.getStatus() == 1) {
            ProgressReport newProgressReportByTaskId = progressReportService.getNewProgressReportByTaskId(report.getTaskId());
            if (newProgressReportByTaskId == null)
                newProgressReportByTaskId = new ProgressReport();

            Task task = taskService.getById(report.getTaskId());
            task.setProgress(newProgressReportByTaskId.getProgress() != null ? newProgressReportByTaskId.getProgress() : "");
            task.setIssuesAndChallenges(newProgressReportByTaskId.getIssuesAndChallenges() != null ? newProgressReportByTaskId.getIssuesAndChallenges() : "");
            task.setRequiresCoordination(newProgressReportByTaskId.getRequiresCoordination() == null ? false : newProgressReportByTaskId.getRequiresCoordination());
            task.setNextSteps(newProgressReportByTaskId.getNextSteps() != null ? newProgressReportByTaskId.getNextSteps() : "");
            task.setHandler(newProgressReportByTaskId.getHandler() != null ? newProgressReportByTaskId.getHandler() : "");
            task.setPhone(newProgressReportByTaskId.getPhone() != null ? newProgressReportByTaskId.getPhone() : "");
            task.setTbFileUrl(newProgressReportByTaskId.getTbFileUrl() != null ? newProgressReportByTaskId.getTbFileUrl() : "");

            taskService.updateCbReport(task);
        }

        return new BaseResponse(HttpStatus.OK.value(), "success", update, Integer.toString(0));
    }
}
