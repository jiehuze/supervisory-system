package com.schedule.supervisory.controller;

import com.schedule.common.BaseResponse;
import com.schedule.supervisory.entity.BzIssueTargetRecord;
import com.schedule.supervisory.service.IBzIssueTargetRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bzIssueTargetRecords")
public class BzIssueTargetRecordController {

    @Autowired
    private IBzIssueTargetRecordService bzIssueTargetRecordService;

    // 根据target_id查询记录
    @GetMapping("/getByTargetId")
    public BaseResponse getByTargetId(@RequestParam Integer targetId) {
        List<BzIssueTargetRecord> bzIssueTargetRecords = bzIssueTargetRecordService.getByTargetId(targetId);
        return new BaseResponse(HttpStatus.OK.value(), "success", bzIssueTargetRecords, Integer.toString(0));
    }

    // 插入新记录
    @PostMapping
    public boolean add(@RequestBody BzIssueTargetRecord bzIssueTargetRecord) {
        return bzIssueTargetRecordService.save(bzIssueTargetRecord);
    }
}