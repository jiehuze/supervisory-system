package com.schedule.supervisory.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.schedule.common.BaseResponse;
import com.schedule.supervisory.dto.TaskCollectDTO;
import com.schedule.supervisory.dto.TaskDTO;
import com.schedule.supervisory.entity.StageNode;
import com.schedule.supervisory.entity.Task;
import com.schedule.supervisory.service.IStageNodeService;
import com.schedule.supervisory.service.ITaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskService taskService;
    @Autowired
    private IStageNodeService stageNodeService;

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

        return new BaseResponse(HttpStatus.OK.value(), "success", 0, Integer.toString(0));
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
}