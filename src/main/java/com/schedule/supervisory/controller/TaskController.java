package com.schedule.supervisory.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.schedule.common.BaseResponse;
import com.schedule.common.Licence;
import com.schedule.supervisory.dto.*;
import com.schedule.supervisory.entity.*;
import com.schedule.supervisory.service.*;
import com.schedule.utils.HttpUtil;
import com.schedule.utils.TaskStatus;
import com.schedule.utils.util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskService taskService;
    @Autowired
    private IStageNodeService stageNodeService;

    @Autowired
    private IProgressReportService progressReportService;

    @Autowired
    private IFieldService fieldService;

    @Autowired
    private IMembershipService membershipService;

    @Autowired
    private IYkbMessageService ykbMessageService;

    @Autowired
    private IConfigService configService;

    @Autowired
    private ParameterDTO parameterDTO;
    @Autowired
    private ICheckService checkService;

    @PostMapping
    public BaseResponse createTask(@RequestBody Task task) {
        taskService.insertTask(task);
        return new BaseResponse(HttpStatus.OK.value(), "success", 0, Integer.toString(0));
    }

    @PostMapping("/batchadd")
    public BaseResponse saveOrUpdateTasks(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                          @RequestHeader(value = "tenant-id", required = false) String tenantId,
                                          @RequestBody List<TaskDTO> taskDTOList) {
        if (!Licence.getLicence()) {
//            String tenantIdex = configService.getTenantId();
            String tenantIdex = configService.getExternConfig("tenant.id");
            System.out.println("+++++++++++=========== tenantId: " + tenantIdex);
            if (!tenantId.equals(tenantIdex))
                return new BaseResponse(HttpStatus.OK.value(), "success", null, Integer.toString(0));
        }
        for (TaskDTO taskDTO : taskDTOList) {
            Task task = taskDTO.getTask();
            if (task.getId() == null) {
                //创建超期时间的任务直接写超时时间
                long taskoverdueDays = 0;

                for (StageNode stageNode : taskDTO.getStageNodes()) {
                    taskoverdueDays = Math.max(util.daysDifference(stageNode.getDeadline()), taskoverdueDays);
                }
                taskoverdueDays = Math.max(util.daysDifference(task.getDeadline()), taskoverdueDays);
                task.setOverdueDays((int) taskoverdueDays);

                Long id = taskService.insertTask(task);
                if (id == null) {
                    return new BaseResponse(HttpStatus.NO_CONTENT.value(), "failed", id, Integer.toString(0));
                }
                if (task.getLeadingDepartmentId() == null || task.getResponsiblePerson() == null) {
                    return new BaseResponse(HttpStatus.NO_CONTENT.value(), "未填写牵头单位和责任人", id, Integer.toString(0));
                }

                ykbMessageService.sendMessageForNewTask(task); // 发送消息

                String[] departmentIds = task.getLeadingDepartmentId().split(",");
                String[] person = task.getResponsiblePerson().split(",");
                for (int i = 0; i < departmentIds.length; i++) {
                    Membership membership = new Membership();
                    membership.setLeadingDepartmentId(departmentIds[i]);
                    membership.setResponsiblePerson(person[i]);

                    membershipService.addOrUpdateMembership(membership);
                }


                for (StageNode stageNode : taskDTO.getStageNodes()) {
                    stageNode.setTaskId((int) id.longValue());
                }
                stageNodeService.batchCreateStageNodes(taskDTO.getStageNodes());
            }
        }
        return new BaseResponse(HttpStatus.OK.value(), "success", 0, Integer.toString(0));
    }

    @PutMapping("/update/{id}")
    public BaseResponse updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        long taskoverdueDays = 0;
        Task task = taskDTO.getTask();
        //创建超期时间的任务直接写超时时间
        if (util.daysDifference(task.getDeadline()) > 0) {
            taskoverdueDays = Math.max(util.daysDifference(task.getDeadline()), taskoverdueDays);
        }

        for (StageNode stageNode : taskDTO.getStageNodes()) {
            stageNode.setTaskId((int) id.longValue());
            taskoverdueDays = Math.max(util.daysDifference(stageNode.getDeadline()), taskoverdueDays);
            if (stageNode.getId() != null) {
                stageNodeService.removeById(stageNode.getId());
            }
        }

        task.setOverdueDays((int) taskoverdueDays);
        taskService.updateTask(task);
        stageNodeService.batchCreateStageNodes(taskDTO.getStageNodes());

        return new BaseResponse(HttpStatus.OK.value(), "success", 0, Integer.toString(0));
    }

    @PutMapping("/report/{id}")
    public BaseResponse reportTask(@PathVariable Long id, @RequestBody Task task) {
        System.out.println(task);
        task.setId(id);
        taskService.updateCbReport(task);

        ProgressReport progressReport = new ProgressReport();
        progressReport.setTaskId(task.getId());
        progressReport.setStatus(3);
        progressReport.setProgress(task.getProgress());
        progressReport.setIssuesAndChallenges(task.getIssuesAndChallenges());
        progressReport.setRequiresCoordination(task.getRequiresCoordination());
        progressReport.setNextSteps(task.getNextSteps());
        progressReport.setHandler(task.getHandler());
        progressReport.setPhone(task.getPhone());
        progressReport.setTbFileUrl(task.getTbFileUrl());
        ProgressReport update = progressReportService.createProgressReport(progressReport);

        return new BaseResponse(HttpStatus.OK.value(), "success", update, Integer.toString(0));
    }

    @GetMapping
    public BaseResponse getAllTasks() {
        List<Task> tasks = taskService.listTasks();
        return new BaseResponse(HttpStatus.OK.value(), "success", tasks, Integer.toString(0));
    }

    @GetMapping("/status/{status}")
    public BaseResponse getTasksByStatus(@PathVariable Integer status) {
        List<Task> tasks = taskService.listTasksByStatus(status);

        return new BaseResponse(HttpStatus.OK.value(), "success", tasks, Integer.toString(0));
    }

    @GetMapping("/{id}")
    public BaseResponse getTask(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return new BaseResponse(HttpStatus.OK.value(), "success", task, Integer.toString(0));
    }

    @GetMapping("/collect")
    public BaseResponse getTaskCollect() {
        TaskCollectDTO taskCollectDTO = new TaskCollectDTO();
        taskCollectDTO.setOverdueTasks(10);
        taskCollectDTO.setCompletedTasks(1);
        taskCollectDTO.setTotalTasks(100);
        taskCollectDTO.setProgressingTasks(10);
        taskCollectDTO.setCompletionRate(80);
        taskCollectDTO.setShortTermCompletionRate(40);

        return new BaseResponse(HttpStatus.OK.value(), "success", taskCollectDTO, Integer.toString(0));
    }

    @GetMapping("/search")
    public BaseResponse searchTasks(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                    @RequestHeader(value = "tenant-id", required = false) String tenantId,
                                    @ModelAttribute TaskSearchDTO queryTask,
                                    @RequestParam(defaultValue = "1") int current,
                                    @RequestParam(defaultValue = "10") int size) {
        System.out.println("permissurl: " + parameterDTO.getPermissionUrl());
        List<DeptDTO> deptDTOs = null;
        HttpUtil httpUtil = new HttpUtil();
        String deptJson = httpUtil.get(parameterDTO.getPermissionUrl(), authorizationHeader, tenantId);
        if (deptJson != null) {
            deptDTOs = JSON.parseArray(deptJson, DeptDTO.class);
            System.out.println("Dept list size: " + deptDTOs.size());
        } else {
            return new BaseResponse(HttpStatus.OK.value(), "鉴权失败，获取权限失败！", false, Integer.toString(0));
        }

        if (!Licence.getLicence()) {
//            String tenantIdex = configService.getTenantId();
            String tenantIdex = configService.getExternConfig("tenant.id");
            System.out.println("+++++++++++=========== tenantId: " + tenantIdex);
            if (!tenantId.equals(tenantIdex))
                return new BaseResponse(HttpStatus.OK.value(), "success", null, Integer.toString(0));
        }

        System.out.println("searchTasks token：" + authorizationHeader);
//        IPage<Task> tasksByConditions = taskService.getTasksByConditions(queryTask, current, size, deptDTOs);
        IPage<Task> tasksByConditions = taskService.queryTasksByConditions(queryTask, current, size, deptDTOs);


        return new BaseResponse(HttpStatus.OK.value(), "success", tasksByConditions, Integer.toString(0));
    }

    @PutMapping("/{taskId}/status")
    public BaseResponse updateTaskStatus(@PathVariable Long taskId,
                                         @RequestParam Integer newStatus) {
        boolean modify = taskService.updateStatusById(taskId, newStatus);

        Task messageTask = taskService.getById(taskId);
        ykbMessageService.sendMessageForCheck(messageTask, newStatus); //办结申请
        return new BaseResponse(HttpStatus.OK.value(), "success", modify, Integer.toString(0));
    }

    @PutMapping("/{taskId}/check")
    public BaseResponse updateTaskCheck(@PathVariable Long taskId,
                                        @RequestParam Integer addStatus,
                                        @RequestParam Integer removeStatus) {
        boolean modify = taskService.updateCheckById(taskId, addStatus, removeStatus);
        return new BaseResponse(HttpStatus.OK.value(), "success", modify, Integer.toString(0));
    }

    @PutMapping("/{taskId}/urgent")
    public BaseResponse updateTaskIsUrgent(@PathVariable Long taskId,
                                           @RequestParam Boolean isUrgent) {
        boolean modify = taskService.updateIsUrgentById(taskId, isUrgent);
        return new BaseResponse(HttpStatus.OK.value(), "success", modify, Integer.toString(0));
    }

    //承办人办结申请
    @PutMapping("/cbapply")
    public BaseResponse partialUpdate(@RequestBody Task task) {
        boolean update = taskService.updateCbApplyDone(task);

        Task messageTask = taskService.getTaskById(task.getId());
        ykbMessageService.sendMessageForCheck(messageTask, task.getStatus());

        return new BaseResponse(HttpStatus.OK.value(), "success", update, Integer.toString(0));
    }

    //所有审核申请接口
    @PutMapping("/closureReviewUpdate")
    public BaseResponse closureReviewUpdate(@RequestBody Task task) {
        boolean update = taskService.updateClosureReview(task);

        Task messageTask = taskService.getById(task.getId());
        ykbMessageService.sendMessageForCheck(messageTask, task.getStatus()); //办结申请
        return new BaseResponse(HttpStatus.OK.value(), "success", update, Integer.toString(0));
    }

    @PutMapping("/updateCancelInfo")
    public BaseResponse updateCancelInfo(@RequestBody Task task) {
        boolean update = taskService.updateCancelInfo(task);

        Task messageTask = taskService.getTaskById(task.getId());
        ykbMessageService.sendMessageForCheck(messageTask, task.getStatus());  //终止申请

        return new BaseResponse(HttpStatus.OK.value(), "success", update, Integer.toString(0));
    }

    @GetMapping("/distinct-sources")
    public BaseResponse getDistinctSources() {
        List<String> distinctSources = taskService.getDistinctSources();
        return new BaseResponse(HttpStatus.OK.value(), "success", distinctSources, Integer.toString(0));
    }

    //统计类接口
    @GetMapping("/statistics")
    public BaseResponse getTaskStatusStatistics(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                                @RequestHeader(value = "tenant-id", required = false) String tenantId,
                                                @ModelAttribute TaskSearchDTO queryTask) {
        System.out.println("permissurl: " + parameterDTO.getPermissionUrl());
        List<DeptDTO> deptDTOs = null;
        if (queryTask.getUnAuth() == null || (queryTask.getUnAuth() != null && queryTask.getUnAuth() == false)) {
            HttpUtil httpUtil = new HttpUtil();
            String deptJson = httpUtil.get(parameterDTO.getPermissionUrl(), authorizationHeader, tenantId);
            if (deptJson != null) {
                deptDTOs = JSON.parseArray(deptJson, DeptDTO.class);
                System.out.println("Dept list size: " + deptDTOs.size());
            } else {
                return new BaseResponse(HttpStatus.OK.value(), "鉴权失败，获取权限失败！", false, Integer.toString(0));
            }
        }

        System.out.println("----------" + queryTask.toString());
        //统计：已办结，未超期的任务数
        TaskStatistics taskStatistics = new TaskStatistics();
        queryTask.setStatus(TaskStatus.TASKSTATUS_COMPLETED.getCode());
        taskStatistics.setTotals(taskService.countTasksNums(queryTask, deptDTOs));
        taskStatistics.setInprogressNums(taskService.countTasksInProgress(queryTask, deptDTOs));
        taskStatistics.setOverdueNums(taskService.countTasksOverdue(queryTask, deptDTOs));
        taskStatistics.setCompleteNums(taskService.countTasksComplete(queryTask, deptDTOs, false));
        taskStatistics.setCompleteOnTimesNums(taskService.countTasksCompleteOnTime(queryTask, deptDTOs));
        taskStatistics.setCompleteShortNums(taskService.countTasksComplete(queryTask, deptDTOs, true));
        queryTask.setStatus(TaskStatus.TASKSTATUS_CANCELLED.getCode());
        taskStatistics.setCancleNums(taskService.countTasksComplete(queryTask, deptDTOs, false));


        return new BaseResponse(HttpStatus.OK.value(), "success", taskStatistics, Integer.toString(0));

    }

    /**
     * 短，中，长期统计
     *
     * @param authorizationHeader
     * @param tenantId
     * @param queryTask
     * @return
     */
    @GetMapping("/statistics_period")
    public BaseResponse countTasksByTaskPeriod(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                               @RequestHeader(value = "tenant-id", required = false) String tenantId,
                                               @ModelAttribute TaskSearchDTO queryTask) {
        System.out.println("permissurl: " + parameterDTO.getPermissionUrl());
        List<DeptDTO> deptDTOs = null;
        List<String> leadingDepartmentIds = new ArrayList<>();
        HttpUtil httpUtil = new HttpUtil();
        String deptJson = httpUtil.get(parameterDTO.getPermissionUrl(), authorizationHeader, tenantId);
        if (deptJson != null) {
            deptDTOs = JSON.parseArray(deptJson, DeptDTO.class);
            System.out.println("Dept list size: " + deptDTOs.size());
        } else {
            return new BaseResponse(HttpStatus.OK.value(), "鉴权失败，获取权限失败！", false, Integer.toString(0));
        }
        if (deptDTOs != null && deptDTOs.size() > 0) {
            for (DeptDTO deptDTO : deptDTOs) {
                leadingDepartmentIds.add(deptDTO.getDeptId());
            }
        }

        List<Map<String, Object>> totals = taskService.countTasksByTaskPeriod2(queryTask, deptDTOs);
        List<Map<String, Object>> complete_totals = taskService.countTasksByTaskPeriodAndStatus2(queryTask, deptDTOs);

        ArrayList<TaskPeriodCount> taskPeriodCounts = new ArrayList<>();
        taskPeriodCounts.add(new TaskPeriodCount(0, 0, 1, "一个月内任务"));
        taskPeriodCounts.add(new TaskPeriodCount(0, 0, 2, "三个月内任务"));
        taskPeriodCounts.add(new TaskPeriodCount(0, 0, 3, "六个月内任务"));
        taskPeriodCounts.add(new TaskPeriodCount(0, 0, 4, "长期任务"));

        for (TaskPeriodCount taskPeriodCount : taskPeriodCounts) {
            for (Map<String, Object> total : totals) {
                if (((Integer) total.get("task_period")).equals(taskPeriodCount.getPeriod())) {
                    taskPeriodCount.setTotal(((Long) total.get("count")).intValue());
                    break;
                }
            }
            for (Map<String, Object> complete_total : complete_totals) {
                if (((Integer) complete_total.get("task_period")).equals(taskPeriodCount.getPeriod())) {
                    taskPeriodCount.setComplete(((Long) complete_total.get("count")).intValue());
                    break;
                }
            }
        }

        return new BaseResponse(HttpStatus.OK.value(), "success", taskPeriodCounts, Integer.toString(0));

    }

    /**
     * 按照所属领域统计
     *
     * @param authorizationHeader
     * @param tenantId
     * @param queryTask
     * @return
     */
    @GetMapping("/statistics_fields")
    public BaseResponse countTasksByTaskField(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                              @RequestHeader(value = "tenant-id", required = false) String tenantId,
                                              @ModelAttribute TaskSearchDTO queryTask) {
        System.out.println("permissurl: " + parameterDTO.getPermissionUrl());
        List<DeptDTO> deptDTOs = null;
        List<String> leadingDepartmentIds = new ArrayList<>();
        HttpUtil httpUtil = new HttpUtil();
        String deptJson = httpUtil.get(parameterDTO.getPermissionUrl(), authorizationHeader, tenantId);
        if (deptJson != null) {
            deptDTOs = JSON.parseArray(deptJson, DeptDTO.class);
            System.out.println("Dept list size: " + deptDTOs.size());
        } else {
            return new BaseResponse(HttpStatus.OK.value(), "鉴权失败，获取权限失败！", false, Integer.toString(0));
        }
        if (deptDTOs != null && deptDTOs.size() > 0) {
            for (DeptDTO deptDTO : deptDTOs) {
                leadingDepartmentIds.add(deptDTO.getDeptId());
            }
        }
        List<Map<String, Object>> totals = taskService.countTasksByFieldId2(queryTask, deptDTOs);
        List<Map<String, Object>> complete_totals = taskService.countTasksByFieldIdAndStatus2(queryTask, deptDTOs);
        List<Field> list = fieldService.getFields(queryTask.getDeleteField());

        ArrayList<TaskFieldCount> taskFieldCounts = new ArrayList<>(list.size());
        for (Field field : list) {
            TaskFieldCount taskFieldCount = new TaskFieldCount();
            taskFieldCount.setFieldId(field.getId());
            taskFieldCount.setFieldName(field.getName());
            for (Map<String, Object> total : totals) {
//                System.out.println("for ++++++++++ field id :" + total.get("field_id") + "  " + field.getId());
                if (((Integer) total.get("field_id")).equals(field.getId().intValue())) {
//                    System.out.println("equals ++++++++++ field id :" + total.get("field_id") + "  " + field.getId());
                    taskFieldCount.setTotal(((Long) total.get("count")).intValue());
                    break;
                }
            }
            for (Map<String, Object> complete_total : complete_totals) {
                if (((Integer) complete_total.get("field_id")).equals(field.getId().intValue())) {
                    taskFieldCount.setComplete(((Long) complete_total.get("count")).intValue());
                    break;
                }
            }
//            System.out.println("-------taskFieldCount: " + taskFieldCount.toString());

            taskFieldCounts.add(taskFieldCount);
        }

        return new BaseResponse(HttpStatus.OK.value(), "success", taskFieldCounts, Integer.toString(0));

    }

    @GetMapping("/update_overdue")
    public BaseResponse updateTaskStatusAndDays() {
        taskService.updateOverdueDays();
        return new BaseResponse(HttpStatus.OK.value(), "success", 0, Integer.toString(0));

    }

    @GetMapping("/getdue")
    public BaseResponse getdue() {
        List<Task> tasksDueInHours = taskService.getTasksDueInHours(15);
        return new BaseResponse(HttpStatus.OK.value(), "success", tasksDueInHours, Integer.toString(0));

    }

    @GetMapping("/getAutoReportInfo")
    public BaseResponse getAutoReportInfo(@ModelAttribute TaskSearchDTO taskSearchDTO) {

        List<Task> tasks = taskService.listTasksBySource(taskSearchDTO.getSource());
        List<Long> taskIds = new ArrayList<>();
        for (Task task : tasks) {
            taskIds.add(task.getId());
        }
        List<ProgressReport> progressReports = progressReportService.getProgressReportByTaskIds(taskIds);
        progressReports.get(0);

        return new BaseResponse(HttpStatus.OK.value(), "success", progressReports.get(0), Integer.toString(0));

    }
}