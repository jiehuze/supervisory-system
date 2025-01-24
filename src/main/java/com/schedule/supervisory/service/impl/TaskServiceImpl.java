package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    @Override
    public IPage<Task> getTasksByConditions(Task queryTask, int pageNum, int pageSize) {
        // 创建分页对象
        Page<Task> page = new Page<>(pageNum, pageSize);

        // 构建查询条件
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();

        if (queryTask.getSource() != null && !queryTask.getSource().isEmpty()) {
            queryWrapper.like(Task::getSource, queryTask.getSource());
        }

        if (queryTask.getContent() != null && !queryTask.getContent().isEmpty()) {
            queryWrapper.like(Task::getContent, queryTask.getContent());
        }

        if (queryTask.getLeadingOfficial() != null && !queryTask.getLeadingOfficial().isEmpty()) {
            queryWrapper.eq(Task::getLeadingOfficial, queryTask.getLeadingOfficial());
        }

        if (queryTask.getLeadingDepartment() != null && !queryTask.getLeadingDepartment().isEmpty()) {
            queryWrapper.eq(Task::getLeadingDepartment, queryTask.getLeadingDepartment());
        }

        if (queryTask.getDeadline() != null) {
            queryWrapper.eq(Task::getDeadline, queryTask.getDeadline());
        }

        if (queryTask.getStatus() != null) {
            queryWrapper.eq(Task::getStatus, queryTask.getStatus());
        }

        // 根据需要添加其他条件...

        // 执行分页查询
        return page(page, queryWrapper);
    }
}