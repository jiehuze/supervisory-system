package com.schedule.supervisory.controller;

import com.schedule.supervisory.entity.Task;
import com.schedule.supervisory.service.ITaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskService taskService;

    @PostMapping
    public void createTask(@RequestBody Task task) {
        taskService.insertTask(task);
    }

    @PostMapping("/batch")
    public void createBatchTasks(@RequestBody List<Task> tasks) {
        taskService.batchInsertTasks(tasks);
    }

    @PutMapping("/{id}")
    public void updateTask(@PathVariable Long id, @RequestBody Task task) {
        task.setId(id);
        taskService.updateTask(task);
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.listTasks();
    }

    @GetMapping("/status/{status}")
    public List<Task> getTasksByStatus(@PathVariable Integer status) {
        return taskService.listTasksByStatus(status);
    }

    // 其他 RESTful API 方法...
}