package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.TaskMapper;
import com.schedule.supervisory.dto.TaskSearchDTO;
import com.schedule.supervisory.entity.Task;
import com.schedule.supervisory.service.ITaskService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    public IPage<Task> getTasksByConditions(TaskSearchDTO queryTask, int pageNum, int pageSize) {

        //todo 读取用户的权限，根据权限判断要读取什么样的数据
        //权限有如下几种：1：承办人，只需要查看本单位下的数据；2：交办人：只需要看本人下的数据；3：承办领导：本部门及下属部门  4：领导：可以看到所有
        //1）交办人只读取自己创建的任务；2）承办人：只看自己负责的任务；3）交办领导：只看自己负责的部门；4）承包领导：只看自己负责的部门
        //所以看获取的人员部门数组；如果数组为空：判断创建人或者责任人；如果不为空，需要查询包含部门的数据
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

        if (queryTask.getLeadingOfficialId() != null && !queryTask.getLeadingOfficialId().isEmpty()) {
            queryWrapper.like(Task::getLeadingOfficialId, queryTask.getLeadingOfficialId());
        }

        // 处理leadingOfficialId模糊查询的情况
        String[] leadingOfficialIds = null;
        if (leadingOfficialIds != null && leadingOfficialIds.length > 0) {
            queryWrapper.and(wrapper -> {
                for (String id : leadingOfficialIds) {
                    if (id != null && !id.trim().isEmpty()) {
                        wrapper.or(w -> w.like(Task::getLeadingOfficialId, id));
                    }
                }
            });
        } else if (queryTask.getUserId() != null && !queryTask.getUserId().isEmpty()) {
            // 使用apply方法添加复杂的OR条件
            queryWrapper.or(wrapper -> wrapper
                    .like(Task::getAssignerId, queryTask.getUserId())
                    .or()
                    .like(Task::getResponsiblePersonId, queryTask.getUserId())
            );
        }

        if (queryTask.getLeadingDepartmentId() != null && !queryTask.getLeadingDepartmentId().isEmpty()) {
            queryWrapper.like(Task::getLeadingDepartmentId, queryTask.getLeadingDepartmentId());
        }

        if (queryTask.getResponsiblePersonId() != null && !queryTask.getResponsiblePersonId().isEmpty()) {
            queryWrapper.like(Task::getResponsiblePersonId, queryTask.getResponsiblePersonId());
        }

        if (queryTask.getDeadline() != null) {
            queryWrapper.eq(Task::getDeadline, queryTask.getDeadline());
        }

        if (queryTask.getStatus() != null) {
            queryWrapper.eq(Task::getStatus, queryTask.getStatus());
        }

        queryWrapper.orderByDesc(Task::getId);

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
        if (task.getTbFileUrl() != null) {
            updateWrapper.set(Task::getTbFileUrl, task.getTbFileUrl());
        }

        return update(null, updateWrapper);
    }

    @Override
    public Long countTasksNums(LocalDateTime createdAtStart, LocalDateTime createdAtEnd, String coOrganizerId, String leadingOfficialId) {
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        // 添加协办单位筛选条件
        if (coOrganizerId != null && !coOrganizerId.isEmpty()) {
            queryWrapper.like(Task::getCoOrganizerId, coOrganizerId);
        }

        // 添加牵头领导筛选条件
        if (leadingOfficialId != null && !leadingOfficialId.isEmpty()) {
            queryWrapper.like(Task::getLeadingOfficialId, leadingOfficialId);
        }

        // 添加创建时间范围的筛选条件
        if (createdAtStart != null && createdAtEnd != null) {
            queryWrapper.between(Task::getCreatedAt, createdAtStart, createdAtEnd);
        }

        return taskMapper.selectCount(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> getStatusStatistics(LocalDateTime createdAtStart, LocalDateTime createdAtEnd, String coOrganizerId) {
        return taskMapper.getStatusStatistics(createdAtStart, createdAtEnd, coOrganizerId);
    }

    @Override
    public Long countTasksCompleteOnTime(LocalDateTime createdAtStart, LocalDateTime createdAtEnd, String coOrganizerId, String leadingOfficialId) {
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();

        // 添加 status 为 6 的条件
        queryWrapper.eq(Task::getStatus, 6);

        // 添加 updated_at 小于等于 deadline 的条件
        // 添加 updated_at 小于等于 deadline 的条件
        queryWrapper.apply("updated_at <= deadline");

        // 添加协办单位筛选条件
        if (coOrganizerId != null && !coOrganizerId.isEmpty()) {
            queryWrapper.like(Task::getCoOrganizerId, coOrganizerId);
        }

        // 添加牵头领导筛选条件
        if (leadingOfficialId != null && !leadingOfficialId.isEmpty()) {
            queryWrapper.like(Task::getLeadingOfficialId, leadingOfficialId);
        }

        // 添加创建时间范围的筛选条件
        if (createdAtStart != null && createdAtEnd != null) {
            queryWrapper.between(Task::getCreatedAt, createdAtStart, createdAtEnd);
        }

        return taskMapper.selectCount(queryWrapper);
    }

    @Override
    public Long countTasksInProgress(LocalDateTime createdAtStart, LocalDateTime createdAtEnd, String coOrganizerId, String leadingOfficialId) {
        return taskMapper.countTasksInProgress(coOrganizerId, createdAtStart, createdAtEnd, leadingOfficialId);
    }

    @Override
    public Long countTasksOverdue(LocalDateTime createdAtStart, LocalDateTime createdAtEnd, String coOrganizerId, String leadingOfficialId) {
        return taskMapper.countTasksOverdue(coOrganizerId, createdAtStart, createdAtEnd, leadingOfficialId);
    }

    @Override
    public Long countTasksComplete(LocalDateTime createdAtStart, LocalDateTime createdAtEnd, String coOrganizerId, String leadingOfficialId, Boolean taskPeriod) {
        return taskMapper.countTasksComplete(coOrganizerId, createdAtStart, createdAtEnd, leadingOfficialId, taskPeriod);
    }

    /**
     * 计算taskPeriod分别为1,2,3的任务总数。
     *
     * @param coOrganizerId  协办单位ID
     * @param createdAtStart 创建时间起始范围
     * @param createdAtEnd   创建时间结束范围
     * @return 符合条件的任务数量列表
     */
    @Override
    public List<Map<String, Object>> countTasksByTaskPeriod(String coOrganizerId, LocalDateTime createdAtStart, LocalDateTime createdAtEnd) {
        return taskMapper.countTasksByTaskPeriod(coOrganizerId, createdAtStart, createdAtEnd);
    }

    /**
     * 计算taskPeriod分别为1,2,3且状态status为6的任务数。
     *
     * @param coOrganizerId  协办单位ID
     * @param createdAtStart 创建时间起始范围
     * @param createdAtEnd   创建时间结束范围
     * @return 符合条件的任务数量列表
     */
    @Override
    public List<Map<String, Object>> countTasksByTaskPeriodAndStatus(String coOrganizerId, LocalDateTime createdAtStart, LocalDateTime createdAtEnd) {
        return taskMapper.countTasksByTaskPeriodAndStatus(coOrganizerId, createdAtStart, createdAtEnd);
    }

    /**
     * 计算根据fieldId分组的所有任务总数。
     *
     * @param coOrganizerId  协办单位ID
     * @param createdAtStart 创建时间起始范围
     * @param createdAtEnd   创建时间结束范围
     * @return 符合条件的任务数量列表
     */
    @Override
    public List<Map<String, Object>> countTasksByFieldId(String coOrganizerId, LocalDateTime createdAtStart, LocalDateTime createdAtEnd) {
        return taskMapper.countTasksByFieldId(coOrganizerId, createdAtStart, createdAtEnd);
    }

    /**
     * 计算根据fieldId分组且状态status为6的任务数。
     *
     * @param coOrganizerId  协办单位ID
     * @param createdAtStart 创建时间起始范围
     * @param createdAtEnd   创建时间结束范围
     * @return 符合条件的任务数量列表
     */
    @Override
    public List<Map<String, Object>> countTasksByFieldIdAndStatus(String coOrganizerId, LocalDateTime createdAtStart, LocalDateTime createdAtEnd) {
        return taskMapper.countTasksByFieldIdAndStatus(coOrganizerId, createdAtStart, createdAtEnd);
    }


    @Override
    public void updateOverdueDays() {
        taskMapper.updateOverdueDays();
    }

    @Override
    public List<Task> getTasksDueInHours(int hours) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureTime = now.plusHours(hours); // 当前时间 + N 小时

        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .isNotNull(Task::getDeadline)  // 确保 deadline 不是 null
                .gt(Task::getDeadline, now)   // deadline 在当前时间之后
                .lt(Task::getDeadline, futureTime)  // deadline - now < N 小时
                .ne(Task::getStatus, 6)  // 排除状态 6
                .ne(Task::getStatus, 9); // 排除状态 9

        return taskMapper.selectList(queryWrapper);
    }

}