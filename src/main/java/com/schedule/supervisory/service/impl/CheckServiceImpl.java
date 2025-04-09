package com.schedule.supervisory.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.CheckMapper;
import com.schedule.supervisory.dto.BzFormDTO;
import com.schedule.supervisory.dto.BzIssueDTO;
import com.schedule.supervisory.dto.ProcessNodeDTO;
import com.schedule.supervisory.entity.*;
import com.schedule.supervisory.service.*;
import com.schedule.utils.HttpUtil;
import com.schedule.utils.util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CheckServiceImpl extends ServiceImpl<CheckMapper, Check> implements ICheckService {
    @Autowired
    private ITaskService taskService;
    @Autowired
    private IStageNodeService stageNodeService;
    @Autowired
    private IProgressReportService progressReportService;
    @Autowired
    private IBzIssueService bzIssueService;
    @Autowired
    private IBzIssueTargetService bzIssueTargetService;
    @Autowired
    private IBzFormService bzFormService;
    @Autowired
    private IBzFormTargetService bzFormTargetService;

    @Autowired
    private IBzFormTargetRecordService bzFormTargetRecordService;
    @Autowired
    private IBzIssueTargetRecordService bzIssueTargetRecordService;

    @Autowired
    private IYkbMessageService ykbMessageService;

    @Autowired
    private CheckMapper checkMapper;

    @Override
    public int insertCheck(Check check) {
        return checkMapper.insert(check);
    }

    @Override
    public boolean checkStatus(Check check) {
        LambdaUpdateWrapper<Check> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Check::getId, check.getId());
        updateWrapper.set(Check::getStatus, check.getStatus());
        return update(updateWrapper);
    }

    @Override
    public Check getByOnlyId(Check check) {
        LambdaQueryWrapper<Check> queryWrapper = new LambdaQueryWrapper<>();
        if (check.getId() != null) {
            queryWrapper.eq(Check::getId, check.getId()); // 查询没有删除
        }
        if (check.getTaskId() != null) {
            queryWrapper.eq(Check::getTaskId, check.getTaskId());
        }
        if (check.getStageId() != null) {
            queryWrapper.eq(Check::getStageId, check.getStageId());
        }
        if (check.getBzFormId() != null) {
            queryWrapper.eq(Check::getBzFormId, check.getBzFormId());
        }
        if (check.getBzIssueId() != null) {
            queryWrapper.eq(Check::getBzIssueId, check.getBzIssueId());
        }
        if (check.getBzFormTargetId() != null) {
            queryWrapper.eq(Check::getBzFormTargetId, check.getBzFormTargetId());
        }
        if (check.getBzIssueTargetId() != null) {
            queryWrapper.eq(Check::getBzIssueTargetId, check.getBzIssueTargetId());
        }
        if (check.getCheckType() != null) {
            queryWrapper.eq(Check::getCheckType, check.getCheckType());
        }
        queryWrapper.orderByDesc(Check::getId);
        queryWrapper.last("LIMIT 1");
        return getOne(queryWrapper);
    }

    /**
     * 所有通过审核，或者驳回审核，都需要将对应的checkStatus相关状态清空
     *
     * @param check
     * @return
     */
    @Override
    public boolean updateCheckStatusByCheckType(Check check) {
        boolean checkStatus = checkStatus(check);
        if (checkStatus) {
            //进度填报 1；阶段性目标办结申请审核 2
            if (check.getTaskId() != null) {
                if (check.getStageId() != null && check.getCheckType() == 2) {
                    taskService.updateCheckById(check.getTaskId(), null, 2);

                } else if (check.getCheckType() == 1) {
                    taskService.updateCheckById(check.getTaskId(), null, 1);
                } else if (check.getCheckType() == 7) {
                    taskService.updateCheckById(check.getTaskId(), null, 7);
                } else if (check.getCheckType() == 8) {
                    taskService.updateCheckById(check.getTaskId(), null, 8);
                }
            }
            //报表牵头人提交审核 3；承办人指标审核 4
            if (check.getBzFormId() != null) {
                if (check.getBzFormTargetId() != null && check.getCheckType() == 4) {
                    bzFormService.updateCheckById(check.getBzFormId(), null, 4);
                    bzFormTargetService.updateCheckById(check.getBzFormTargetId(), null, 4);
                } else if (check.getCheckType() == 3) {
                    bzFormService.updateCheckById(check.getBzFormId(), null, 3);
                }
            }
            //报表牵头人提交审核 3；承办人指标审核 4
            if (check.getBzIssueId() != null) {
                if (check.getBzIssueTargetId() != null && check.getCheckType() == 6) {
                    bzIssueService.updateCheckById(check.getBzIssueId(), null, 4);
                    bzIssueTargetService.updateCheckById(check.getBzIssueTargetId(), null, 4);
                } else if (check.getCheckType() == 5) {
                    bzIssueService.updateCheckById(check.getBzIssueId(), null, 3);
                }
            }
        }
        return true;
    }

    @Override
    public Check getByProcessInstanceId(String processInstanceId) {
        LambdaQueryWrapper<Check> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Check::getProcessInstanceId, processInstanceId);
        queryWrapper.orderByDesc(Check::getId);
        queryWrapper.last("LIMIT 1");
        return getOne(queryWrapper);
    }

    /**
     * 更新数据状态，根据审核结果，只有通过或者驳回的时候才修改状态，其他时候不修改状态
     *
     * @param check
     * @return
     */
    @Override
    public boolean updateCheckInfoToTarget(Check check) {
        boolean checkStatus = checkStatus(check);
        switch (check.getCheckType()) {
            case 1: //填报申请,需要更新填报
                ProgressReport progressReport = JSON.parseObject(check.getDataJson(), new TypeReference<ProgressReport>() {
                });
                if (check.getStatus() == 2) { //审核通过
                    Task task = new Task();
                    task.setId(check.getTaskId());
                    task.setStatus(2);
                    task.setProgress(progressReport.getProgress());
                    task.setIssuesAndChallenges(progressReport.getIssuesAndChallenges());
                    task.setRequiresCoordination(progressReport.getRequiresCoordination());
                    task.setNextSteps(progressReport.getNextSteps());
                    task.setHandler(progressReport.getHandler());
                    task.setPhone(progressReport.getPhone());
                    task.setTbFileUrl(progressReport.getTbFileUrl());
                    taskService.updateCbReport(task);
                    progressReportService.updateStatus(progressReport.getId().intValue(), 3);
                } else if (check.getStatus() == 3) {
                    progressReportService.updateStatus(progressReport.getId().intValue(), 5);
                    taskService.updateStatusById(check.getTaskId(), 2); //设置为正常推进状态

                }
                taskService.updateCheckById(check.getTaskId(), null, 1);
                break;
            case 2: //阶段性审核，更新阶段性审核数据
                if (check.getStatus() == 2) { //通过审核
                    stageNodeService.updateStatusById(check.getStageId().intValue(), 2);
                    StageNode stage = stageNodeService.getById(check.getStageId());
                    if (stage.getOverdueDays() > 0) {
                        //需要更新状态
                        List<StageNode> stageNodeForOverdues = stageNodeService.getStageNodeForOverdue(check.getTaskId());
                        long taskoverdueDays = 0;
                        for (StageNode sn : stageNodeForOverdues) {
                            taskoverdueDays = Math.max(util.daysDifference(sn.getDeadline()), taskoverdueDays);
                        }
                        taskService.updateOverdueDays(check.getTaskId(), (int) taskoverdueDays);
                    }
                } else if (check.getStatus() == 3) {
                    stageNodeService.updateStatusById(check.getStageId().intValue(), 1); //修改为正常推进
                }
                taskService.updateStatusById(check.getTaskId(), 2);
                taskService.updateCheckById(check.getTaskId(), null, 2);
                break;
            case 7: // 办结审核，更新办结状态为6
                if (check.getStatus() == 2) { //通过审核
                    taskService.updateStatusById(check.getTaskId(), 6);
                } else if (check.getStatus() == 3) {
                    taskService.updateStatusById(check.getTaskId(), 2); //修改成正常推进中
                }
                taskService.updateCheckById(check.getTaskId(), null, 7);
                break;
            case 8: //终结审核，更新终结状态为9
                if (check.getStatus() == 2) { //通过审核
                    taskService.updateStatusById(check.getTaskId(), 9);
                } else if (check.getStatus() == 3) {
                    taskService.updateStatusById(check.getTaskId(), 2); //修改成正常推进中
                }
                taskService.updateCheckById(check.getTaskId(), null, 8);
                break;
            case 3: //报表清单编辑审核
                //获取表单数据
                if (check.getStatus() == 2) { //通过审核
                    BzFormDTO bzFormDTO = JSON.parseObject(check.getDataJson(), new TypeReference<BzFormDTO>() {
                    });
                    if (bzFormDTO != null) {
                        BzForm bzForm = bzFormDTO.getBzForm();
                        for (BzFormTarget bzFormTarget : bzFormDTO.getBzFormTargetList()) {
                            bzFormTarget.setBzFormId(bzForm.getId());
                            //需要修改牵头单位到每个target中去
                            bzFormTarget.setLeadingDepartment(bzForm.getLeadingDepartment());
                            bzFormTarget.setLeadingDepartmentId(bzForm.getLeadingDepartmentId());

                            //将每个指标的责任单位写入到清单中
                            bzForm.setResponsibleDept(util.joinString(bzForm.getResponsibleDept(), bzFormTarget.getDept()));
                            bzForm.setResponsibleDeptId(util.joinString(bzForm.getResponsibleDeptId(), bzFormTarget.getDeptId()));
                        }

                        bzFormTargetService.saveOrUpdateBatch(bzFormDTO.getBzFormTargetList());
                        boolean upate = bzFormService.updateBzFrom(bzForm);
                    }
                } else if (check.getStatus() == 3) {

                }
                bzFormService.updateCheckById(check.getBzFormId(), null, 3);
                break;
            case 4: //报表指标审核
                BzFormTarget bzFormTarget = JSON.parseObject(check.getDataJson(), new TypeReference<BzFormTarget>() {
                });

                if (check.getStatus() == 2) { //通过审核
                    if (bzFormTarget != null) {
                        boolean progress = bzFormTargetService.updateProgress(bzFormTarget);

                        bzFormTargetRecordService.updateStatus(bzFormTarget.getId(), 2);
                    }
                } else if (check.getStatus() == 3) {
                    if (bzFormTarget != null) {
                        bzFormTargetRecordService.updateStatus(bzFormTarget.getId(), 3);
                    }
                }
                bzFormTargetService.updateCheckById(check.getBzFormTargetId(), null, 4);
                break;
            case 5: //问题清单审核
                if (check.getStatus() == 2) { //通过审核
                    BzIssueDTO bzIssueDTO = JSON.parseObject(check.getDataJson(), new TypeReference<BzIssueDTO>() {
                    });
                    if (bzIssueDTO != null) {
                        BzIssue bzIssue = bzIssueDTO.getBzIssue();
                        for (BzIssueTarget bzIssueTarget : bzIssueDTO.getBzIssueTargetList()) {
                            bzIssueTarget.setBzIssueId(bzIssue.getId());
                            //需要修改牵头单位到每个target中去
                            bzIssueTarget.setLeadingDepartment(bzIssue.getLeadingDepartment());
                            bzIssueTarget.setLeadingDepartmentId(bzIssue.getLeadingDepartmentId());

                            //将每个指标的责任单位写入到清单中
                            bzIssue.setResponsibleDept(util.joinString(bzIssue.getResponsibleDept(), bzIssueTarget.getDept()));
                            bzIssue.setResponsibleDeptId(util.joinString(bzIssue.getResponsibleDeptId(), bzIssueTarget.getDeptId()));
                        }

                        bzIssueTargetService.saveOrUpdateBatch(bzIssueDTO.getBzIssueTargetList());
                        boolean upate = bzIssueService.updateBzIssue(bzIssue);
                    }
                } else if (check.getStatus() == 3) {

                }
                bzIssueService.updateCheckById(check.getBzIssueId(), null, 3);
                break;
            case 6: //问题指标审核
                BzIssueTarget bzIssueTarget = JSON.parseObject(check.getDataJson(), new TypeReference<BzIssueTarget>() {
                });
                if (check.getStatus() == 2) { //通过审核
                    if (bzIssueTarget != null) {
                        boolean progress = bzIssueTargetService.updateProgress(bzIssueTarget);

                        bzIssueTargetRecordService.updateStatus(bzIssueTarget.getId(), 2);
                    }
                } else if (check.getStatus() == 3) {
                    bzIssueTargetRecordService.updateStatus(bzIssueTarget.getId(), 3);
                }
                bzIssueTargetService.updateCheckById(check.getBzIssueTargetId(), null, 4);
                break;
        }

        return true;
    }

    @Override
    public void executeAfterDelay(String url, String authorizationHeader, String tenantId, Check check) throws InterruptedException {
        logTime("任务开始执行时间：" + java.time.LocalDateTime.now());
        Thread.sleep(5000L); // 模拟等待10秒
        logTime("任务完成时间：" + java.time.LocalDateTime.now());
        HttpUtil httpUtil = new HttpUtil();
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("flowId", check.getFlowId());
        requestBody.put("processInstanceId", check.getProcessInstanceId());

        logTime("check start request body: " + JSON.toJSONString(requestBody));
        //获取节点数据,需要异步处理，等等10秒左右
        String formatData = httpUtil.post(url, authorizationHeader, tenantId, JSON.toJSONString(requestBody));
        if (formatData == null) {
            logTime(url + " request failed");
            return;
        }
        // 解析为Node对象列表
        List<ProcessNodeDTO> nodeList = JSON.parseObject(formatData, new TypeReference<List<ProcessNodeDTO>>() {
        });
        String userIds = "";
        boolean sendFlag = true;
        for (ProcessNodeDTO nodeDTO : nodeList) {
            logTime("=====> check node: " + nodeDTO.toString());
            if (nodeDTO.getStatus() < 2) {
                List<ProcessNodeDTO.UserVo> userVoList = nodeDTO.getUserVoList();
                for (ProcessNodeDTO.UserVo userVo : userVoList) {
                    if (userVo.getStatus() < 2) {
                        userIds = util.joinString(userIds, userVo.getId());
                    } else if (userVo.getStatus() == 2) { //说明是会签，有一个人为2，其他人就不要再发送数据消息了
                        sendFlag = false;
                    }
                }
                //说明已经取到要执行的节点，直接退出即可
                if (userIds.length() > 1) {
                    break;
                }
            }
        }
        logTime("=====> check node userIds: " + userIds);
        //说明没有要审核的人了，就是审核完成了
        if (userIds.length() <= 1) {
            logTime("Complete -------->> check process instance complete, check type：" + check.getCheckType() + "     check id: " + check.getId() + " check status： " + check.getStatus());
            check.setStatus(2);
            updateCheckInfoToTarget(check);
            return;
        }

        if (sendFlag) {
            //发消息
            ykbMessageService.sendMessageForCheckNew(check, userIds);
        }
        switch (check.getCheckType()) {
            case 1: //填报申请
                taskService.updateCheckProcess(check.getTaskId(), null, check.getProcessInstanceId(), userIds);
                break;
            case 2: //阶段性审核
            case 7: // 办结审核
            case 8: //终结审核
                taskService.updateCheckProcess(check.getTaskId(), check.getProcessInstanceId(), null, userIds);
                break;
            case 3: //报表清单编辑审核
                bzFormService.updateCheckProcess(check.getBzFormId(), check.getProcessInstanceId(), userIds);
                break;
            case 4: //报表指标审核
                bzFormTargetService.updateCheckProcess(check.getBzFormTargetId(), check.getProcessInstanceId(), userIds);
                break;
            case 5: //问题清单审核
                bzIssueService.updateCheckProcess(check.getBzIssueId(), check.getProcessInstanceId(), userIds);
                break;
            case 6: //问题指标审核
                bzIssueTargetService.updateCheckProcess(check.getBzIssueTargetId(), check.getProcessInstanceId(), userIds);
                break;
        }
    }

    private void logTime(String taskName) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(time + " " + taskName);
    }
}