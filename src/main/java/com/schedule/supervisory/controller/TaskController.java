package com.schedule.supervisory.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.schedule.common.BaseResponse;
import com.schedule.supervisory.entity.Task;
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

    @PostMapping
    public BaseResponse createTask(@RequestBody Task task) {
        taskService.insertTask(task);
        return new BaseResponse(HttpStatus.OK.value(), "success", 0, Integer.toString(0));
    }

    @PostMapping("/batch")
    public BaseResponse createBatchTasks(@RequestBody List<Task> tasks) {
        taskService.batchInsertTasks(tasks);

        return new BaseResponse(HttpStatus.OK.value(), "success", 0, Integer.toString(0));
    }

    @PutMapping("/update/{id}")
    public BaseResponse updateTask(@PathVariable Long id, @RequestBody Task task) {
        System.out.println(task);
        task.setId(id);
        taskService.updateTask(task);

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

    @GetMapping("/search")
    public BaseResponse searchTasks(@ModelAttribute Task queryTask,
                                    @RequestParam(defaultValue = "1") int pageNum,
                                    @RequestParam(defaultValue = "10") int pageSize) {
        IPage<Task> tasksByConditions = taskService.getTasksByConditions(queryTask, pageNum, pageSize);

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
}