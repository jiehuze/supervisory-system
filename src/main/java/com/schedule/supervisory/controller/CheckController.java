package com.schedule.supervisory.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.schedule.common.BaseResponse;
import com.schedule.supervisory.dto.CheckStartDTO;
import com.schedule.supervisory.dto.ParameterDTO;
import com.schedule.supervisory.dto.ProcessNodeDTO;
import com.schedule.supervisory.entity.Check;
import com.schedule.supervisory.entity.Task;
import com.schedule.supervisory.service.*;
import com.schedule.utils.HttpUtil;
import com.schedule.utils.TaskStatus;
import com.schedule.utils.util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

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
        boolean save = checkService.save(check);
        CheckStartDTO checkStartDTO = new CheckStartDTO();
        HashMap<String, String> paramMap = new HashMap<>();
        switch (check.getCheckType()) {
            case 1: //填报申请
                taskService.updateCheckById(check.getTaskId(), 1, 0); //填报审核
                checkStartDTO.setFlowId("P20250312054230198JQIIT");
                break;
            case 2: //阶段性审核
                taskService.updateCheckById(check.getTaskId(), 2, 0);
                stageNodeService.updateStatusById(check.getStageId().intValue(), 4); //审核中
                checkStartDTO.setFlowId("P20250312055145385L8CB4");
                Task taskst = taskService.getById(check.getTaskId());
                paramMap.put("m85i4x1dgshw6", taskst.getAssignerId());
                break;
            case 3: //报表清单编辑审核
                bzFormService.updateCheckById(check.getBzFormId(), 3, 0);
                checkStartDTO.setFlowId("P20250312055555772BEE9J");

                break;
            case 4: //报表指标审核
                bzFormTargetService.updateCheckById(check.getBzFormTargetId(), 4, null);
                checkStartDTO.setFlowId("P20250312055509220T4MVJ");

                break;
            case 5: //问题清单审核
                checkStartDTO.setFlowId("P20250312055555772BEE9J");
                bzIssueService.updateCheckById(check.getBzIssueId(), 3, 0);

                break;
            case 6: //问题指标审核
                bzIssueTargetService.updateCheckById(check.getBzIssueTargetId(), 4, null);
                checkStartDTO.setFlowId("P20250312055509220T4MVJ");

                break;
            case 7: // 办结审核
                taskService.updateCheckById(check.getTaskId(), 7, 0); //填报审核
                checkStartDTO.setFlowId("P20250312054853669F7BFK");
                Task taskCm = taskService.getById(check.getTaskId());
                paramMap.put("m85i04a1nuxd2", taskCm.getAssignerId());
                paramMap.put("m85hstanyga4a", taskCm.getLeadingOfficialId());
                break;
            case 8: //终结审核
                taskService.updateCheckById(check.getTaskId(), 8, 0); //填报审核
                checkStartDTO.setFlowId("P20250312055005786PBMBA");
                Task taskCn = taskService.getById(check.getTaskId());
                paramMap.put("m85i04a1nuxd2", taskCn.getAssignerId());
                paramMap.put("m85hstanyga4a", taskCn.getLeadingOfficialId());
                break;
        }

        paramMap.put("check_json", check.getDataJson());

        //发哦送审核请求
        HttpUtil httpUtil = new HttpUtil();
        String processInstanceId = httpUtil.post(parameterDTO.getCheckStart(), authorizationHeader, tenantId, JSON.toJSONString(checkStartDTO));
        if (processInstanceId == null) {
            return new BaseResponse(HttpStatus.OK.value(), parameterDTO.getCheckStart() + " request failed", null, Integer.toString(0));
        }

        String formatData = httpUtil.post(parameterDTO.getCheckFormat(), authorizationHeader, tenantId, JSON.toJSONString(checkStartDTO));
        if (formatData == null) {
            return new BaseResponse(HttpStatus.OK.value(), parameterDTO.getCheckFormat() + " request failed", null, Integer.toString(0));
        }
        // 解析为Node对象列表
        List<ProcessNodeDTO> nodeList = JSON.parseObject(formatData, new TypeReference<List<ProcessNodeDTO>>() {
        });
        String userIds = "";
        for (ProcessNodeDTO nodeDTO : nodeList) {
            if (nodeDTO.getStatus() < 2) {
                List<ProcessNodeDTO.UserVo> userVoList = nodeDTO.getUserVoList();
                for (ProcessNodeDTO.UserVo userVo : userVoList) {
                    if (userVo.getStatus() < 2) {
                        util.joinString(userIds, userVo.getId());
                    }
                }
            }
        }

        switch (check.getCheckType()) {
            case 1: //填报审核

                break;
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
        System.out.println("============ check: " + check.toString());
        Check checkInfo = checkService.getByOnlyId(check);
        return new BaseResponse(HttpStatus.OK.value(), "success", checkInfo, Integer.toString(0));
    }
}