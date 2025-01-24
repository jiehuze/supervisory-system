package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.TaskMapper;
import com.schedule.supervisory.entity.Task;
import com.schedule.supervisory.service.ITaskService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements ITaskService {

    private final TaskMapper taskMapper;

    public TaskServiceImpl(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    @Override
    public void insertTask(Task task) {
        taskMapper.insertTask(task);
    }

    @Override
    public void batchInsertTasks(List<Task> tasks) {
        taskMapper.batchInsertTasks(tasks);
    }

    @Override
    public void updateTask(Task task) {
        taskMapper.updateTask(task);
    }

    @Override
    public List<Task> listTasks() {
        return taskMapper.listTasks();
    }

    @Override
    public List<Task> listTasksByStatus(Integer status) {
        return taskMapper.listTasksByStatus(status);
    }
}