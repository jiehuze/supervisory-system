package com.schedule.supervisory.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.schedule.common.BaseResponse;
import com.schedule.supervisory.dto.CheckStartDTO;
import com.schedule.supervisory.dto.ParameterDTO;
import com.schedule.supervisory.dto.ProcessCallBackDTO;
import com.schedule.supervisory.dto.TokenRespDTO;
import com.schedule.supervisory.entity.*;
import com.schedule.supervisory.service.*;
import com.schedule.utils.HttpUtil;
import com.schedule.utils.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/check")
public class CheckController {

    @Autowired
    private ICheckService checkService;

    @Autowired
    private ITaskService taskService;

    @Autowired
    private IStageNodeService stageNodeService;
    @Autowired
    private IProgressReportService progressReportService;

    @Autowired
    private IBzFormService bzFormService;

    @Autowired
    private IBzIssueService bzIssueService;

    @Autowired
    private IBzFormTargetService bzFormTargetService;
    @Autowired
    private IBzIssueTargetService bzIssueTargetService;

    @Autowired
    private IYkbMessageService ykbMessageService;

    @Autowired
    private ParameterDTO parameterDTO;

    @PostMapping("/add")
    public BaseResponse add(@RequestBody Check check) {
        boolean save = checkService.save(check);

        if (save) {
            //进度填报 1；阶段性目标办结申请审核 2
            if (check.getTaskId() != null) {
                if (check.getStageId() != null) {
                    taskService.updateCheckById(check.getTaskId(), 2, 0);
                    stageNodeService.updateStatusById(check.getStageId().intValue(), 4); //审核中
                } else {
                    taskService.updateCheckById(check.getTaskId(), 1, 0); //填报审核
                }
                Task messageTask = taskService.getTaskById(check.getTaskId());
                ykbMessageService.sendMessageForCheck(messageTask, TaskStatus.TASKSTATUS_REPORT.getCode());
            }
            //报表牵头人提交审核 3；承办人指标审核 4
            if (check.getBzFormId() != null) {
                if (check.getBzFormTargetId() != null) {
//                    bzFormService.updateCheckById(check.getBzFormId(), 4, 0); //不需要写数据库，实时读取
                    bzFormTargetService.updateCheckById(check.getBzFormTargetId(), 4, null);
                } else {
                    bzFormService.updateCheckById(check.getBzFormId(), 3, 0);
                }
            }
            //报表牵头人提交审核 3；承办人指标审核 4
            if (check.getBzIssueId() != null) {
                if (check.getBzIssueTargetId() != null) {
//                    bzIssueService.updateCheckById(check.getBzIssueId(), 4, 0);//不需要写数据库，实时读取
                    bzIssueTargetService.updateCheckById(check.getBzIssueTargetId(), 4, null);
                } else {
                    bzIssueService.updateCheckById(check.getBzIssueId(), 3, 0);
                }
            }
        }

        return new BaseResponse(HttpStatus.OK.value(), "success", save, Integer.toString(0));
    }

    @PostMapping("/start")
    public BaseResponse start(@RequestBody Check check,
                              @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                              @RequestHeader(value = "tenant-id", required = false) String tenantId) {
        CheckStartDTO checkStartDTO = new CheckStartDTO();
        HashMap<String, Object> paramMap = new HashMap<>();
        switch (check.getCheckType()) {
            case 1: //填报申请
                taskService.updateCheckById(check.getTaskId(), 1, 0); //填报审核
                taskService.updateStatusById(check.getTaskId(), 12);//审核中

                //将填报数据进行历史存储,方便进行查询
                ProgressReport progressReport = JSON.parseObject(check.getDataJson(), new TypeReference<ProgressReport>() {
                });
                progressReport.setFlowId("P20250312054230198JQIIT");
                ProgressReport progressReportNew = progressReportService.createProgressReport(progressReport);
                check.setDataJson(JSON.toJSONString(progressReportNew)); //需要更新下，以为新增的progressReport数据没有记录id，后面审核的时候无法获取到

                checkStartDTO.setFlowId("P20250312054230198JQIIT");
                Task taskrp = taskService.getById(check.getTaskId());
                paramMap.put("submitDeptIds", taskrp.getLeadingDepartmentId().split(","));
                ykbMessageService.sendMessageForCheck(taskrp, TaskStatus.TASKSTATUS_REPORT.getCode());
                break;
            case 2: //阶段性审核
                taskService.updateCheckById(check.getTaskId(), 2, 0);
                stageNodeService.updateStatusById(check.getStageId().intValue(), 4); //审核中
                taskService.updateStatusById(check.getTaskId(), 12);//审核中
                checkStartDTO.setFlowId("P20250312055145385L8CB4");
                Task taskst = taskService.getById(check.getTaskId());
                paramMap.put("m85i4x1dgshw6", taskst.getAssignerId());
                paramMap.put("submitDeptIds", taskst.getLeadingDepartmentId().split(","));

                break;
            case 3: //报表清单编辑审核
                bzFormService.updateCheckById(check.getBzFormId(), 3, 0);
                checkStartDTO.setFlowId("P20250312055555772BEE9J");
                BzForm bzform = bzFormService.getById(check.getBzFormId());
                paramMap.put("submitDeptIds", bzform.getLeadingDepartmentId().split(","));
                break;
            case 4: //报表指标审核
                bzFormTargetService.updateCheckById(check.getBzFormTargetId(), 4, null);
                checkStartDTO.setFlowId("P20250312055509220T4MVJ");
                BzFormTarget bzFormTarget = bzFormTargetService.getById(check.getBzFormTargetId());
                paramMap.put("submitDeptIds", bzFormTarget.getDeptId().split(","));
                break;
            case 5: //问题清单审核
                checkStartDTO.setFlowId("P20250312055555772BEE9J");
                bzIssueService.updateCheckById(check.getBzIssueId(), 3, 0);
                BzIssue bzIssue = bzIssueService.getById(check.getBzIssueId());
                paramMap.put("submitDeptIds", bzIssue.getLeadingDepartmentId().split(","));
                break;
            case 6: //问题指标审核
                bzIssueTargetService.updateCheckById(check.getBzIssueTargetId(), 4, null);
                checkStartDTO.setFlowId("P20250312055509220T4MVJ");
                BzIssueTarget bzIssueTarget = bzIssueTargetService.getById(check.getBzIssueTargetId());
                paramMap.put("submitDeptIds", bzIssueTarget.getDeptId().split(","));
                break;
            case 7: // 办结审核
                taskService.updateCheckById(check.getTaskId(), 7, 0); //填报审核
                taskService.updateStatusById(check.getTaskId(), 12);//审核中
                checkStartDTO.setFlowId("P20250312054853669F7BFK");
                Task taskCm = taskService.getById(check.getTaskId());
                paramMap.put("m85i04a1nuxd2", taskCm.getAssignerId());
                paramMap.put("m85hstanyga4a", taskCm.getLeadingOfficialId());
                paramMap.put("submitDeptIds", taskCm.getLeadingDepartmentId().split(","));

                ykbMessageService.sendMessageForCheck(taskCm, 4);

                break;
            case 8: //终结审核
                taskService.updateCheckById(check.getTaskId(), 8, 0); //填报审核
                taskService.updateStatusById(check.getTaskId(), 12);//审核中
                checkStartDTO.setFlowId("P20250312055005786PBMBA");
                Task taskCn = taskService.getById(check.getTaskId());
                paramMap.put("m85i04a1nuxd2", taskCn.getAssignerId());
                paramMap.put("m85hstanyga4a", taskCn.getLeadingOfficialId());
                paramMap.put("submitDeptIds", taskCn.getLeadingDepartmentId().split(","));

                ykbMessageService.sendMessageForCheck(taskCn, 7);
                break;
        }

        paramMap.put("check_json", check.getDataJson());
        checkStartDTO.setParamMap(paramMap);

        //发送审核请求
        System.out.println("check start request body: " + JSON.toJSONString(checkStartDTO));
        HttpUtil httpUtil = new HttpUtil();
        String processInstanceId = httpUtil.post(parameterDTO.getCheckStart(), authorizationHeader, tenantId, JSON.toJSONString(checkStartDTO));
        if (processInstanceId == null) {
            return new BaseResponse(HttpStatus.OK.value(), parameterDTO.getCheckStart() + " request failed", null, Integer.toString(0));
        }

        check.setProcessInstanceId(processInstanceId);
        check.setFlowId(checkStartDTO.getFlowId());
        boolean save = checkService.save(check);

        if (check.getCheckType() == 1) {//填报审核需要特殊处理，保存流水号
            ProgressReport progressReport = JSON.parseObject(check.getDataJson(), new TypeReference<ProgressReport>() {
            });
            progressReport.setFlowId(check.getFlowId());
            progressReport.setProcessInstanceId(check.getProcessInstanceId());
            progressReportService.updateProgressReportCheckInfo(progressReport);
        }

        try {
            checkService.executeAfterDelay(parameterDTO.getCheckFormat(), authorizationHeader, tenantId, check);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new BaseResponse(HttpStatus.OK.value(), "success", 0, Integer.toString(0));
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
        System.out.println("============ check: " + check.toString());
        Check checkInfo = checkService.getByOnlyId(check);
        return new BaseResponse(HttpStatus.OK.value(), "success", checkInfo, Integer.toString(0));
    }

    /**
     * 退回
     *
     * @param processCallBackDTO
     * @return
     */
    @PostMapping("callback")
    public BaseResponse callback(@RequestBody ProcessCallBackDTO processCallBackDTO) {
        Check check = checkService.getByProcessInstanceId(processCallBackDTO.getProcessInstanceId());
        if (check == null) {
            return new BaseResponse(HttpStatus.OK.value(), "[callback] ProcessInstanceId is not exist", 0, Integer.toString(0));
        }

        if (processCallBackDTO.getStatus() == 3) { //驳回申请,3就是被驳回，1是审核中
            check.setStatus(3);
            boolean checkInfo = checkService.updateCheckInfoToTarget(check);
            return new BaseResponse(HttpStatus.OK.value(), "success", checkInfo, Integer.toString(0));
        }

        //如果是通过审核了，需要延迟处理，获取节点数据，并根据节点数据进行是否审核完成处理
        try {
            HttpUtil httpUtil = new HttpUtil();
            TokenRespDTO tokenRespDTO = httpUtil.oauthen2(parameterDTO.getAuthUrl());
            checkService.executeAfterDelay(
                    parameterDTO.getCheckFormat(),
                    String.format("%s %s", tokenRespDTO.getToken_type(), tokenRespDTO.getAccess_token()),
                    "1877665103373783042",
                    check);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return new BaseResponse(HttpStatus.OK.value(), "success", 0, Integer.toString(0));
    }
}