package com.schedule.supervisory.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.schedule.common.BaseResponse;
import com.schedule.supervisory.dto.*;
import com.schedule.supervisory.entity.ProgressReport;
import com.schedule.supervisory.entity.StageNode;
import com.schedule.supervisory.entity.Task;
import com.schedule.supervisory.service.IProgressReportService;
import com.schedule.supervisory.service.IStageNodeService;
import com.schedule.supervisory.service.ITaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    @PostMapping
    public BaseResponse createTask(@RequestBody Task task) {
        taskService.insertTask(task);
        return new BaseResponse(HttpStatus.OK.value(), "success", 0, Integer.toString(0));
    }

    @PostMapping("/batchadd")
    public BaseResponse saveOrUpdateTasks(@RequestBody List<TaskDTO> taskDTOList) {
        for (TaskDTO taskDTO : taskDTOList) {
            Task task = taskDTO.getTask();
            if (task.getId() == null) {
                Long id = taskService.insertTask(task);
                if (id == null) {
                    return new BaseResponse(HttpStatus.NO_CONTENT.value(), "failed", id, Integer.toString(0));
                }
                for (StageNode stageNode : taskDTO.getStageNodes()) {
                    stageNode.setTaskId((int) id.longValue());
                }
                stageNodeService.batchCreateStageNodes(taskDTO.getStageNodes());
            }
        }
        return new BaseResponse(HttpStatus.OK.value(), "success", 0, Integer.toString(0));
    }

//    @PostMapping("/batch")
//    public BaseResponse createBatchTasks(@RequestBody List<Task> tasks) {
//        taskService.batchInsertTasks(tasks);
//
//        return new BaseResponse(HttpStatus.OK.value(), "success", 0, Integer.toString(0));
//    }

    @PutMapping("/update/{id}")
    public BaseResponse updateTask(@PathVariable Long id, @RequestBody Task task) {
        System.out.println(task);
        task.setId(id);
        taskService.updateTask(task);

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
    public BaseResponse searchTasks(@ModelAttribute Task queryTask,
                                    @RequestParam(defaultValue = "1") int current,
                                    @RequestParam(defaultValue = "10") int size) {
        IPage<Task> tasksByConditions = taskService.getTasksByConditions(queryTask, current, size);

        return new BaseResponse(HttpStatus.OK.value(), "success", tasksByConditions, Integer.toString(0));
    }

    @PutMapping("/{taskId}/status")
    public BaseResponse updateTaskStatus(@PathVariable Long taskId,
                                         @RequestParam Integer newStatus) {
        boolean modify = taskService.updateStatusById(taskId, newStatus);
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
        return new BaseResponse(HttpStatus.OK.value(), "success", update, Integer.toString(0));
    }

    //交办人审核申请
    @PutMapping("/closureReviewUpdate")
    public BaseResponse closureReviewUpdate(@RequestBody Task task) {
        boolean update = taskService.updateClosureReview(task);
        return new BaseResponse(HttpStatus.OK.value(), "success", update, Integer.toString(0));
    }

    @PutMapping("/updateCancelInfo")
    public BaseResponse updateCancelInfo(@RequestBody Task task) {
        boolean update = taskService.updateCancelInfo(task);
        return new BaseResponse(HttpStatus.OK.value(), "success", update, Integer.toString(0));
    }

    @GetMapping("/distinct-sources")
    public BaseResponse getDistinctSources() {
        List<String> distinctSources = taskService.getDistinctSources();
        return new BaseResponse(HttpStatus.OK.value(), "success", distinctSources, Integer.toString(0));
    }

    //统计类接口
    @GetMapping("/statistics")
    public BaseResponse getTaskStatusStatistics(
            @RequestParam(required = false) LocalDateTime createdAtStart,
            @RequestParam(required = false) LocalDateTime createdAtEnd,
            @RequestParam(required = false) String coOrganizerId) {
        //统计：已办结，未超期的任务数
        TaskStatistics taskStatistics = new TaskStatistics();
        taskStatistics.setTotals(taskService.countTasksNums(createdAtStart, createdAtEnd, coOrganizerId));
        taskStatistics.setInprogressNums(taskService.countTasksInProgress(createdAtStart, createdAtEnd, coOrganizerId));
        taskStatistics.setOverdueNums(taskService.countTasksOverdue(createdAtStart, createdAtEnd, coOrganizerId));
        taskStatistics.setCompleteNums(taskService.countTasksComplete(createdAtStart, createdAtEnd, coOrganizerId));
        taskStatistics.setCompletOnTimesNums(taskService.countTasksCompleteOnTime(createdAtStart, createdAtEnd, coOrganizerId));

        return new BaseResponse(HttpStatus.OK.value(), "success", taskStatistics, Integer.toString(0));

    }

    //统计：已办结，未超期的任务数
    @GetMapping("/statistics_complete")
    public BaseResponse getTaskCompleteOntime(
            @RequestParam(required = false) LocalDateTime createdAtStart,
            @RequestParam(required = false) LocalDateTime createdAtEnd,
            @RequestParam(required = false) String coOrganizerId) {
        Long count = taskService.countTasksCompleteOnTime(createdAtStart, createdAtEnd, coOrganizerId);
        return new BaseResponse(HttpStatus.OK.value(), "success", count, Integer.toString(0));

    }

    @GetMapping("/statistics_period")
    public BaseResponse countTasksByTaskPeriod(
            @RequestParam(required = false) LocalDateTime createdAtStart,
            @RequestParam(required = false) LocalDateTime createdAtEnd,
            @RequestParam(required = false) String coOrganizerId) {
        List<Map<String, Object>> totals = taskService.countTasksByTaskPeriod(coOrganizerId, createdAtStart, createdAtEnd);
        List<Map<String, Object>> complete_totals = taskService.countTasksByTaskPeriodAndStatus(coOrganizerId, createdAtStart, createdAtEnd);
        TaskPeriodCount taskPeriodCount = new TaskPeriodCount();
        taskPeriodCount.setTotals(totals);
        taskPeriodCount.setComplete_totals(complete_totals);

        return new BaseResponse(HttpStatus.OK.value(), "success", taskPeriodCount, Integer.toString(0));

    }

    @GetMapping("/statistics_fields")
    public BaseResponse countTasksByTaskField(
            @RequestParam(required = false) LocalDateTime createdAtStart,
            @RequestParam(required = false) LocalDateTime createdAtEnd,
            @RequestParam(required = false) String coOrganizerId) {
        List<Map<String, Object>> totals = taskService.countTasksByFieldId(coOrganizerId, createdAtStart, createdAtEnd);
        List<Map<String, Object>> complete_totals = taskService.countTasksByFieldIdAndStatus(coOrganizerId, createdAtStart, createdAtEnd);
        TaskFileldCount taskFileldCount = new TaskFileldCount();
        taskFileldCount.setTotals(totals);
        taskFileldCount.setComplete_totals(complete_totals);

        return new BaseResponse(HttpStatus.OK.value(), "success", taskFileldCount, Integer.toString(0));

    }

//    @GetMapping("/update_overdue")
//    public BaseResponse updateTaskStatusAndDays() {
//        return new BaseResponse(HttpStatus.OK.value(), "success", statusStatistics, Integer.toString(0));
//
//    }
}