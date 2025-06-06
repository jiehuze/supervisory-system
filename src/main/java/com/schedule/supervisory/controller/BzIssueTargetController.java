package com.schedule.supervisory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.supervisory.dto.BzSearchDTO;
import com.schedule.supervisory.entity.*;
import com.schedule.supervisory.service.IBzIssueService;
import com.schedule.supervisory.service.IBzIssueTargetRecordService;
import com.schedule.supervisory.service.IBzIssueTargetService;
import com.schedule.utils.util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/issuetargets")
public class BzIssueTargetController {
    @Autowired
    private IBzIssueService bzIssueService;
    @Autowired
    private IBzIssueTargetService bzIssueTargetService;

    @Autowired
    private IBzIssueTargetRecordService bzIssueTargetRecordService;

    // 分页查询
    @GetMapping("/page")
    public Page<BzIssueTarget> page(@RequestParam int current, @RequestParam int size) {
        return bzIssueTargetService.page(new Page<>(current, size));
    }

    // 不分页查询
    @GetMapping
    public List<BzIssueTarget> getAll() {
        return bzIssueTargetService.list();
    }

    // 插入数据
    @PostMapping
    public boolean add(@RequestBody BzIssueTarget bzIssueTarget) {
        return bzIssueTargetService.save(bzIssueTarget);
    }

    // 批量插入数据
    @PostMapping("/batch")
    public BaseResponse batchInsert(@RequestBody List<BzIssueTarget> bzIssueTargets) {
        boolean saveBatch = false;
        if (bzIssueTargets.size() > 0) {
            BzSearchDTO bzSearchDTO = new BzSearchDTO();
            bzSearchDTO.setId(bzIssueTargets.get(0).getBzIssueId().longValue());
            bzSearchDTO.setBzIssueId(bzIssueTargets.get(0).getBzIssueId().longValue());
            List<BzIssueTarget> bzIssueTargetList = bzIssueTargetService.getByIssueId(bzSearchDTO, null);
            for (BzIssueTarget bzIssueTarget : bzIssueTargetList) {
                bzIssueTargetService.removeById(bzIssueTarget.getId());
            }
            BzIssue bzIssue = bzIssueService.getById(bzSearchDTO.getBzIssueId());
            //写入牵头单位
            for (BzIssueTarget bzIssueTarget : bzIssueTargets) {
                bzIssueTarget.setLeadingDepartment(bzIssue.getLeadingDepartment());
                bzIssueTarget.setLeadingDepartmentId(bzIssue.getLeadingDepartmentId());
            }
            saveBatch = bzIssueTargetService.saveBatch(bzIssueTargets);

            //更新时，写入责任单位
            for (BzIssueTarget bzIssueTarget : bzIssueTargets) {
                bzIssue.setResponsibleDept(util.joinString(bzIssue.getResponsibleDept(), bzIssueTarget.getDept()));
                bzIssue.setResponsibleDeptId(util.joinString(bzIssue.getResponsibleDeptId(), bzIssueTarget.getDeptId()));
            }
            bzIssueService.updateById(bzIssue);
        }
        return new BaseResponse(HttpStatus.OK.value(), "success", saveBatch, Integer.toString(0));
    }

    // 更新数据
    @PutMapping("/update")
    public BaseResponse update(@RequestBody BzIssueTarget bzIssueTarget) {
        boolean update = bzIssueTargetService.updateProgressById(bzIssueTarget);
        return new BaseResponse(HttpStatus.OK.value(), "success", update, Integer.toString(0));
    }

    // 更新进度
    @PutMapping("/progress")
    public BaseResponse updateProgress(@RequestBody BzIssueTarget bzIssueTarget) {
        boolean progress = bzIssueTargetService.updateProgress(bzIssueTarget);
        BzIssueTargetRecord bzIssueTargetRecord = new BzIssueTargetRecord();
        bzIssueTargetRecord.setTargetId(bzIssueTarget.getId());
        bzIssueTargetRecord.setIssue(bzIssueTarget.getIssues());
        bzIssueTargetRecord.setWorkProgress(bzIssueTarget.getWorkProgress());
        bzIssueTargetRecord.setUpdatedBy(bzIssueTarget.getOperatorId());
        bzIssueTargetRecord.setOperator(bzIssueTarget.getOperator());
        bzIssueTargetRecord.setOperatorId(bzIssueTarget.getOperatorId());
        bzIssueTargetRecord.setTargetName(bzIssueTarget.getName());
        bzIssueTargetRecord.setPredictedGear(bzIssueTarget.getPredictedGear());
        bzIssueTargetRecordService.insertBzIssueTargetRecord(bzIssueTargetRecord);

        return new BaseResponse(HttpStatus.OK.value(), "success", progress, Integer.toString(0));
    }

    //审核进度
    @PutMapping("/review")
    public BaseResponse reviewProgress(@RequestBody BzIssueTarget bzIssueTarget) {
        boolean progress = bzIssueTargetService.reviewProgress(bzIssueTarget);
        return new BaseResponse(HttpStatus.OK.value(), "success", progress, Integer.toString(0));
    }

    @DeleteMapping("/delete/{id}")
    public BaseResponse reviewProgress(@PathVariable Long id) {
        System.out.println("++++++++++++++id: " + id);
        boolean delete = bzIssueTargetService.removeById(id);
        return new BaseResponse(HttpStatus.OK.value(), "success", delete, Integer.toString(0));
    }
}