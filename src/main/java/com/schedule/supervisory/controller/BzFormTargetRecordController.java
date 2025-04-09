package com.schedule.supervisory.controller;

import com.schedule.common.BaseResponse;
import com.schedule.supervisory.entity.BzFormTargetRecord;
import com.schedule.supervisory.service.IBzFormTargetRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/bzFormTargetRecords")
public class BzFormTargetRecordController {

    @Autowired
    private IBzFormTargetRecordService bzFormTargetRecordService;

    // 根据target_id查询记录
    @GetMapping("/getByTargetId")
    public BaseResponse getByTargetId(@RequestParam Integer targetId) {
        List<BzFormTargetRecord> bzFormTargetRecords = bzFormTargetRecordService.getByTargetId(targetId);
        return new BaseResponse(HttpStatus.OK.value(), "success", bzFormTargetRecords, Integer.toString(0));
    }

    @GetMapping("/getGearHistoryByTargetId")
    public BaseResponse getGearHistoryByTargetId(@RequestParam Integer targetId) {
        List<BzFormTargetRecord> historyByTargetId = bzFormTargetRecordService.getHistoryByTargetId(targetId);

        return new BaseResponse(HttpStatus.OK.value(), "success", historyByTargetId, Integer.toString(0));
    }

    // 插入新记录
    @PostMapping
    public boolean add(@RequestBody BzFormTargetRecord bzFormTargetRecord) {
        return bzFormTargetRecordService.save(bzFormTargetRecord);
    }
}