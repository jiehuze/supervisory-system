package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
    public Long insertTask(Task task) {
//        taskMapper.insertTask(task);
        boolean result = save(task);
        if (result) {
            return task.getId();
        } else {
            return null;
        }
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
    public List<String> getDistinctSources() {
        return taskMapper.selectDistinctSources();
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
            queryWrapper.like(Task::getLeadingOfficial, queryTask.getLeadingOfficial());
        }

        if (queryTask.getLeadingDepartment() != null && !queryTask.getLeadingDepartment().isEmpty()) {
            queryWrapper.like(Task::getLeadingDepartment, queryTask.getLeadingDepartment());
        }

        if (queryTask.getDeadline() != null) {
            queryWrapper.eq(Task::getDeadline, queryTask.getDeadline());
        }

        if (queryTask.getStatus() != null) {
            queryWrapper.eq(Task::getStatus, queryTask.getStatus());
        }

//        if (queryTask.getId() != null) {
//            queryWrapper.eq(Task::getId, queryTask.getId());
//        }
        return page(page, queryWrapper);
    }

    @Override
    public boolean updateStatusById(Long taskId, Integer newStatus) {
        LambdaUpdateWrapper<Task> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Task::getId, taskId)
                .set(Task::getStatus, newStatus);
        return update(updateWrapper);
    }

    @Override
    public boolean updateIsUrgentById(Long taskId, Boolean isUrgent) {
        LambdaUpdateWrapper<Task> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Task::getId, taskId)
                .set(Task::getIsUrgent, isUrgent);
        return update(updateWrapper);
    }

    @Override
    public boolean updateInstructionById(Long taskId, String instrunction) {
        LambdaUpdateWrapper<Task> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Task::getId, taskId)
                .set(Task::getInstruction, instrunction);
        return update(updateWrapper);
    }

    @Override
    public Task getTaskById(Long id) {
        return getById(id);
    }

    @Override
    public boolean updateCbApplyDone(Task task) {
        LambdaUpdateWrapper<Task> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Task::getId, task.getId());

        if (task.getStatus() != null) {
            updateWrapper.set(Task::getStatus, task.getStatus());
        }
        if (task.getCbDoneDesc() != null) {
            updateWrapper.set(Task::getCbDoneDesc, task.getCbDoneDesc());
        }
        if (task.getCbDoneFile() != null) {
            updateWrapper.set(Task::getCbDoneFile, task.getCbDoneFile());
        }

        return update(null, updateWrapper);
    }

    @Override
    public boolean updateClosureReview(Task task) {
        LambdaUpdateWrapper<Task> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Task::getId, task.getId());

        if (task.getStatus() != null) {
            updateWrapper.set(Task::getStatus, task.getStatus());
        }
        if (task.getClosureReviewResult() != null) {
            updateWrapper.set(Task::getClosureReviewResult, task.getClosureReviewResult());
        }
        if (task.getClosureReviewDesc() != null) {
            updateWrapper.set(Task::getClosureReviewDesc, task.getClosureReviewDesc());
        }
        if (task.getClosureReviewFile() != null) {
            updateWrapper.set(Task::getClosureReviewFile, task.getClosureReviewFile());
        }

        return update(null, updateWrapper);
    }

    @Override
    public boolean updateCancelInfo(Task task) {
        LambdaUpdateWrapper<Task> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Task::getId, task.getId());

        if (task.getStatus() != null) {
            updateWrapper.set(Task::getStatus, task.getStatus());
        }
        if (task.getCancelDesc() != null) {
            updateWrapper.set(Task::getCancelDesc, task.getCancelDesc());
        }
        if (task.getCancelFile() != null) {
            updateWrapper.set(Task::getCancelFile, task.getCancelFile());
        }

        return update(null, updateWrapper);
    }

    @Override
    public boolean updateCbReport(Task task) {
        LambdaUpdateWrapper<Task> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Task::getId, task.getId());

        if (task.getStatus() != null) {
            updateWrapper.set(Task::getStatus, task.getStatus());
        }
        if (task.getProgress() != null) {
            updateWrapper.set(Task::getProgress, task.getProgress());
        }
        if (task.getIssuesAndChallenges() != null) {
            updateWrapper.set(Task::getIssuesAndChallenges, task.getIssuesAndChallenges());
        }
        if (task.getRequiresCoordination() != null) {
            updateWrapper.set(Task::getRequiresCoordination, task.getRequiresCoordination());
        }
        if (task.getNextSteps() != null) {
            updateWrapper.set(Task::getNextSteps, task.getNextSteps());
        }
        if (task.getHandler() != null) {
            updateWrapper.set(Task::getHandler, task.getHandler());
        }
        if (task.getPhone() != null) {
            updateWrapper.set(Task::getPhone, task.getPhone());
        }

        return update(null, updateWrapper);
    }
}