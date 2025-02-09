package com.schedule.supervisory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.supervisory.dto.DuchaReportCreationDTO;
import com.schedule.supervisory.dto.ReportUpdateDTO;
import com.schedule.supervisory.entity.DuchaReport;
import com.schedule.supervisory.service.IDuchaReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/duchareport")
public class DuchaReportController {

    @Autowired
    private IDuchaReportService duchaReportService;

    @GetMapping("/list")
    public Page<DuchaReport> list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return duchaReportService.page(new Page<>(pageNum, pageSize));
    }

    // 插入新记录
    @PostMapping("/add")
    public boolean addDuchaReport(@RequestBody DuchaReport duchaReport) {
        return duchaReportService.save(duchaReport);
    }

    //生成报告
    @PostMapping("/create")
    public BaseResponse generateReport(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                       @RequestHeader(value = "tenant-id", required = false) String tenantId,
                                       @RequestBody DuchaReportCreationDTO reportCreationDTO) {
        boolean create = duchaReportService.generateReportFromTasks(reportCreationDTO, authorizationHeader, tenantId);
        return new BaseResponse(HttpStatus.OK.value(), "success", create, Integer.toString(0));
    }

    // 分页查询方法，通过报送人和领导ID进行过滤
    @GetMapping("/search")
    public BaseResponse searchReports(
            @RequestParam(value = "submitterId", required = false) String submitterId,
            @RequestParam(value = "leadingOfficialId", required = false) String leadingOfficialId,
            @RequestParam(value = "reportName", required = false) String reportName,
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {

        Page<DuchaReport> duchaReportPage = duchaReportService.searchReports(submitterId, leadingOfficialId, reportName, size, size);
        return new BaseResponse(HttpStatus.OK.value(), "success", duchaReportPage, Integer.toString(0));
    }

    @PutMapping("/submit")
    public BaseResponse submitReport(@RequestBody ReportUpdateDTO reportSubmissionDTO) {
        boolean update = duchaReportService.updateSubmitById(reportSubmissionDTO.getDuchaReportId(), reportSubmissionDTO);
        return new BaseResponse(HttpStatus.OK.value(), "success", update, Integer.toString(0));
    }

    @GetMapping("/delete")
    public BaseResponse deleteReports(@RequestParam(value = "duchaReportId", required = false) Long duchaReportId) {
        boolean delete = duchaReportService.deleteById(duchaReportId);
        return new BaseResponse(HttpStatus.OK.value(), "success", delete, Integer.toString(0));
    }

    @PutMapping("/upload")
    public BaseResponse uploadReport(@RequestBody ReportUpdateDTO reportSubmissionDTO) {
        boolean update = duchaReportService.updateReportFileById(reportSubmissionDTO.getDuchaReportId(), reportSubmissionDTO);
        return new BaseResponse(HttpStatus.OK.value(), "success", update, Integer.toString(0));
    }
}