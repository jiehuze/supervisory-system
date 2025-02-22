package com.schedule.supervisory.controller;

import com.schedule.common.BaseResponse;
import com.schedule.supervisory.entity.Check;
import com.schedule.supervisory.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/check")
public class CheckController {

    @Autowired
    private ICheckService checkService;

    @Autowired
    private ITaskService taskService;

    @Autowired
    private IBzFormService bzFormService;

    @Autowired
    private IBzIssueService bzIssueService;

    @Autowired
    private IBzFormTargetService bzFormTargetService;
    @Autowired
    private IBzIssueTargetService bzIssueTargetService;

    @PostMapping("/add")
    public BaseResponse add(@RequestBody Check check) {
        boolean save = checkService.save(check);

        if (save) {
            //进度填报 1；阶段性目标办结申请审核 2
            if (check.getTaskId() != null) {
                if (check.getStageId() != null) {
                    taskService.updateCheckById(check.getTaskId(), 2, 3);
                } else {
                    taskService.updateCheckById(check.getTaskId(), 1, 0);
                }
            }
            //报表牵头人提交审核 3；承办人指标审核 4
            if (check.getBzFormId() != null) {
                if (check.getBzFormTargetId() != null) {
                    bzFormService.updateCheckById(check.getBzFormId(), 4, 0);
                    bzFormTargetService.updateCheckById(check.getBzFormTargetId(), 4, null);
                } else {
                    bzFormService.updateCheckById(check.getBzFormId(), 3, 0);
                }
            }
            //报表牵头人提交审核 3；承办人指标审核 4
            if (check.getBzIssueId() != null) {
                if (check.getBzIssueTargetId() != null) {
                    bzIssueService.updateCheckById(check.getBzIssueId(), 4, 0);
                    bzIssueTargetService.updateCheckById(check.getBzIssueTargetId(), 4, null);
                } else {
                    bzIssueService.updateCheckById(check.getBzIssueId(), 3, 0);
                }
            }
        }

        return new BaseResponse(HttpStatus.OK.value(), "success", save, Integer.toString(0));
    }


    /**
     * 审核填报相关数据
     */
    @PutMapping("/status")
    public BaseResponse update(@ModelAttribute Check check) {
        boolean checkStatus = checkService.checkStatus(check);
        if (checkStatus) {
            Check updatedCheck = checkService.getById(check.getId());
            //进度填报 1；阶段性目标办结申请审核 2
            if (updatedCheck.getTaskId() != null) {
                if (updatedCheck.getStageId() != null && updatedCheck.getCheckType() == 2) {
                    taskService.updateCheckById(updatedCheck.getTaskId(), null, 2);
                } else if (updatedCheck.getCheckType() == 1) {
                    taskService.updateCheckById(updatedCheck.getTaskId(), null, 1);
                }
            }
            //报表牵头人提交审核 3；承办人指标审核 4
            if (updatedCheck.getBzFormId() != null) {
                if (updatedCheck.getBzFormTargetId() != null && updatedCheck.getCheckType() == 4) {
                    bzFormService.updateCheckById(updatedCheck.getBzFormId(), null, 4);
                    bzFormTargetService.updateCheckById(updatedCheck.getBzFormTargetId(), null, 4);
                } else if (updatedCheck.getCheckType() == 3) {
                    bzFormService.updateCheckById(updatedCheck.getBzFormId(), null, 3);
                }
            }
            //报表牵头人提交审核 3；承办人指标审核 4
            if (updatedCheck.getBzIssueId() != null) {
                if (updatedCheck.getBzIssueTargetId() != null && updatedCheck.getCheckType() == 6) {
                    bzIssueService.updateCheckById(updatedCheck.getBzIssueId(), null, 4);
                    bzIssueTargetService.updateCheckById(updatedCheck.getBzIssueTargetId(), null, 4);
                } else if (updatedCheck.getCheckType() == 5) {
                    bzIssueService.updateCheckById(updatedCheck.getBzIssueId(), null, 3);
                }
            }
        }

        return new BaseResponse(HttpStatus.OK.value(), "success", checkStatus, Integer.toString(0));
    }

    @GetMapping("/get")
    public BaseResponse getByTaskIdAndStageId(@ModelAttribute Check check) {
        Check checkInfo = checkService.getByOnlyId(check);
        return new BaseResponse(HttpStatus.OK.value(), "success", checkInfo, Integer.toString(0));
    }
}