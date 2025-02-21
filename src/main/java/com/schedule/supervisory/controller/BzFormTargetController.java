package com.schedule.supervisory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.supervisory.entity.BzFormTarget;
import com.schedule.supervisory.entity.BzFormTargetRecord;
import com.schedule.supervisory.entity.Task;
import com.schedule.supervisory.service.IBzFormTargetRecordService;
import com.schedule.supervisory.service.IBzFormTargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/formtargets")
public class BzFormTargetController {

    @Autowired
    private IBzFormTargetService bzFormTargetService;

    @Autowired
    private IBzFormTargetRecordService bzFormTargetRecordService;

    // 分页查询
    @GetMapping("/page")
    public Page<BzFormTarget> page(@RequestParam int current, @RequestParam int size) {
        return bzFormTargetService.page(new Page<>(current, size));
    }

    // 不分页查询
    @GetMapping
    public List<BzFormTarget> getAll() {
        return bzFormTargetService.list();
    }

    // 插入数据
    @PostMapping
    public boolean add(@RequestBody BzFormTarget bzFormTarget) {
        return bzFormTargetService.save(bzFormTarget);
    }

    // 批量插入数据
    @PostMapping("/batch")
    public BaseResponse batchInsert(@RequestBody List<BzFormTarget> bzFormTargets) {
        boolean saveBatch = bzFormTargetService.saveBatch(bzFormTargets);
        return new BaseResponse(HttpStatus.OK.value(), "success", saveBatch, Integer.toString(0));
    }

    // 更新数据
    @PutMapping("/update")
    public BaseResponse update(@RequestBody BzFormTarget bzFormTarget) {
        boolean update = bzFormTargetService.updateProgressById(bzFormTarget);
        return new BaseResponse(HttpStatus.OK.value(), "success", update, Integer.toString(0));
    }

    // 更新进度
    @PutMapping("/progress")
    public BaseResponse updateProgress(@RequestBody BzFormTarget bzFormTarget) {
        boolean progress = bzFormTargetService.updateProgress(bzFormTarget);
        BzFormTargetRecord bzFormTargetRecord = new BzFormTargetRecord();
        bzFormTargetRecord.setTargetId(bzFormTarget.getId());
        bzFormTargetRecord.setIssue(bzFormTarget.getIssues());
        bzFormTargetRecord.setWorkProgress(bzFormTarget.getWorkProgress());
        bzFormTargetRecordService.insertBzFormTargetRecord(bzFormTargetRecord);

        return new BaseResponse(HttpStatus.OK.value(), "success", progress, Integer.toString(0));
    }

    //审核进度
    @PutMapping("/review")
    public BaseResponse reviewProgress(@RequestBody BzFormTarget bzFormTarget) {
        boolean progress = bzFormTargetService.reviewProgress(bzFormTarget);
        return new BaseResponse(HttpStatus.OK.value(), "success", progress, Integer.toString(0));
    }

    @DeleteMapping("/delete/{id}")
    public BaseResponse reviewProgress(@PathVariable Long id) {
        System.out.println("++++++++++++++id: " + id);
        boolean delete = bzFormTargetService.removeById(id);
        return new BaseResponse(HttpStatus.OK.value(), "success", delete, Integer.toString(0));
    }
}