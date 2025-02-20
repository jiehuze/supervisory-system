package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.TaskMapper;
import com.schedule.supervisory.dto.DeptDTO;
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
    public List<Task> listTasksBySource(String source) {
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Task::getSource, source);
        return taskMapper.selectList(queryWrapper);
    }

    @Override
    public List<Task> ListTasksOverdue() {
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .isNotNull(Task::getDeadline)  // 确保 deadline 不是 null
                .gt(Task::getDeadline, now)   // deadline 在当前时间之后
                .ne(Task::getStatus, 6)  // 排除状态 6
                .ne(Task::getStatus, 9); // 排除状态 9
        return taskMapper.selectList(queryWrapper);
    }

    @Override
    public IPage<Task> getTasksByConditions(TaskSearchDTO queryTask, int pageNum, int pageSize, List<DeptDTO> deptDTOs) {

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
        if (deptDTOs != null && deptDTOs.size() > 0) {
            queryWrapper.and(wrapper -> {
                for (DeptDTO deptDTO : deptDTOs) {
                    wrapper.or(w -> w.like(Task::getLeadingDepartmentId, deptDTO.getDeptId())); //牵头单位
                    wrapper.or(w -> w.like(Task::getCoOrganizerId, deptDTO.getDeptId())); //增加协办单位查询
                }
            });
        } else if (queryTask.getUserId() != null && !queryTask.getUserId().isEmpty()) {
            // 使用apply方法添加复杂的OR条件
            queryWrapper.and(wrapper -> wrapper
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
        if (queryTask.getTaskPeriod() != null) {
            queryWrapper.eq(Task::getTaskPeriod, queryTask.getTaskPeriod());
        }

        if (queryTask.getStatus() != null) {
            if (queryTask.getStatus() == 3) {
                queryWrapper.ne(Task::getStatus, 6);
                queryWrapper.ne(Task::getStatus, 9);
//                queryWrapper.apply("updated_at > deadline");
                queryWrapper.gt(Task::getOverdueDays, 0); //超期时间大于0

            } else {
                queryWrapper.eq(Task::getStatus, queryTask.getStatus());
            }
        } else if (queryTask.getUnfinished() != null && queryTask.getUnfinished()) { //未完成任务
            queryWrapper.ne(Task::getStatus, 6);
            queryWrapper.ne(Task::getStatus, 9);
        }

        // 使用 CASE WHEN 实现自定义排序
        // 使用 CASE 语句来按照特定顺序排序
        String caseSortSql = "CASE status " +
                "WHEN 3 THEN 1 " +
                "WHEN 2 THEN 2 " +
                "WHEN 1 THEN 3 " +
                "WHEN 4 THEN 4 " +
                "WHEN 5 THEN 5 " +
                "WHEN 7 THEN 6 " +
                "WHEN 8 THEN 7 " +
                "WHEN 10 THEN 8 " +
                "WHEN 11 THEN 9 " +
                "WHEN 6 THEN 10 " +
                "WHEN 9 THEN 11 " +
                "END, " + " overdue_days DESC, id DESC";
//        queryWrapper.orderByDesc(Task::getOverdueDays);
        queryWrapper.last("ORDER BY " + caseSortSql);
        // 使用 CASE 语句按 status 排序
//        queryWrapper.orderByDesc(Task::getId);

        return page(page, queryWrapper);
    }

    @Override
    public boolean updateStatusById(Long taskId, Integer newStatus) {
        LambdaUpdateWrapper<Task> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Task::getId, taskId)
                .set(Task::getStatus, newStatus);
        if (newStatus == 6 || newStatus == 9) {
            updateWrapper.set(Task::getCompletedAt, LocalDateTime.now());
        }
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
    public Long countTasksNums(TaskSearchDTO queryTask, List<DeptDTO> deptDTOs) {
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        // 添加协办单位筛选条件
        if (queryTask.getCoOrganizerId() != null && !queryTask.getCoOrganizerId().isEmpty()) {
            queryWrapper.like(Task::getCoOrganizerId, queryTask.getCoOrganizerId());
        }
        if (queryTask.getLeadingDepartmentId() != null && !queryTask.getLeadingDepartmentId().isEmpty()) {
            queryWrapper.like(Task::getLeadingDepartmentId, queryTask.getLeadingDepartmentId());
        }
        if (queryTask.getSource() != null && !queryTask.getSource().isEmpty()) {
            queryWrapper.like(Task::getSource, queryTask.getSource());
        }

        // 添加牵头领导筛选条件
        if (queryTask.getLeadingOfficialId() != null && !queryTask.getLeadingOfficialId().isEmpty()) {
            queryWrapper.like(Task::getLeadingOfficialId, queryTask.getLeadingOfficialId());
        }

        // 处理leadingOfficialId模糊查询的情况
        if (deptDTOs != null && deptDTOs.size() > 0) {
            queryWrapper.and(wrapper -> {
                for (DeptDTO deptDTO : deptDTOs) {
                    wrapper.or(w -> w.like(Task::getLeadingDepartmentId, deptDTO.getDeptId()));
                }
            });
        } else if (queryTask.getUserId() != null && !queryTask.getUserId().isEmpty()) {
            // 使用apply方法添加复杂的OR条件
            queryWrapper.and(wrapper -> wrapper
                    .like(Task::getAssignerId, queryTask.getUserId())
                    .or()
                    .like(Task::getResponsiblePersonId, queryTask.getUserId())
            );
        }

        // 添加创建时间范围的筛选条件
        if (queryTask.getCreatedAtStart() != null && queryTask.getCreatedAtEnd() != null) {
            queryWrapper.between(Task::getCreatedAt, queryTask.getCreatedAtStart(), queryTask.getCreatedAtEnd());
        }

        return taskMapper.selectCount(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> getStatusStatistics(LocalDateTime createdAtStart, LocalDateTime createdAtEnd, String coOrganizerId) {
        return taskMapper.getStatusStatistics(createdAtStart, createdAtEnd, coOrganizerId);
    }

    @Override
    public Long countTasksCompleteOnTime(TaskSearchDTO queryTask, List<DeptDTO> deptDTOs) {
        System.out.println("++++++++= countTasksCompleteOnTime");
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();

        // 添加 status 为 6 的条件
        queryWrapper.eq(Task::getStatus, 6);

        // 添加 updated_at 小于等于 deadline 的条件
        // 添加 updated_at 小于等于 deadline 的条件
//        queryWrapper.apply("updated_at <= deadline");
        queryWrapper.eq(Task::getOverdueDays, 0); //超期时间为0

        // 添加协办单位筛选条件
        if (queryTask.getCoOrganizerId() != null && !queryTask.getCoOrganizerId().isEmpty()) {
            queryWrapper.like(Task::getCoOrganizerId, queryTask.getCoOrganizerId());
        }
        if (queryTask.getLeadingDepartmentId() != null && !queryTask.getLeadingDepartmentId().isEmpty()) {
            queryWrapper.like(Task::getLeadingDepartmentId, queryTask.getLeadingDepartmentId());
        }
        if (queryTask.getSource() != null && !queryTask.getSource().isEmpty()) {
            queryWrapper.like(Task::getSource, queryTask.getSource());
        }

        // 添加牵头领导筛选条件
        if (queryTask.getLeadingOfficialId() != null && !queryTask.getLeadingOfficialId().isEmpty()) {
            queryWrapper.like(Task::getLeadingOfficialId, queryTask.getLeadingOfficialId());
        }
        // 处理leadingOfficialId模糊查询的情况
        if (deptDTOs != null && deptDTOs.size() > 0) {
            queryWrapper.and(wrapper -> {
                for (DeptDTO deptDTO : deptDTOs) {
                    wrapper.or(w -> w.like(Task::getLeadingDepartmentId, deptDTO.getDeptId()));
                }
            });
        } else if (queryTask.getUserId() != null && !queryTask.getUserId().isEmpty()) {
            // 使用apply方法添加复杂的OR条件
            queryWrapper.and(wrapper -> wrapper
                    .like(Task::getAssignerId, queryTask.getUserId())
                    .or()
                    .like(Task::getResponsiblePersonId, queryTask.getUserId())
            );
        }
        // 添加创建时间范围的筛选条件
        if (queryTask.getCreatedAtStart() != null && queryTask.getCreatedAtEnd() != null) {
            queryWrapper.between(Task::getCreatedAt, queryTask.getCreatedAtStart(), queryTask.getCreatedAtEnd());
        }

        return taskMapper.selectCount(queryWrapper);
    }

    @Override
    public Long countTasksInProgress(TaskSearchDTO queryTask, List<DeptDTO> deptDTOs) {
//        return taskMapper.countTasksInProgress(coOrganizerId, createdAtStart, createdAtEnd, leadingOfficialId);
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();

        // 添加协办单位筛选条件
        if (queryTask.getCoOrganizerId() != null && !queryTask.getCoOrganizerId().isEmpty()) {
            queryWrapper.like(Task::getCoOrganizerId, queryTask.getCoOrganizerId());
        }
        if (queryTask.getLeadingDepartmentId() != null && !queryTask.getLeadingDepartmentId().isEmpty()) {
            queryWrapper.like(Task::getLeadingDepartmentId, queryTask.getLeadingDepartmentId());
        }
        if (queryTask.getSource() != null && !queryTask.getSource().isEmpty()) {
            queryWrapper.like(Task::getSource, queryTask.getSource());
        }

        // 添加牵头领导筛选条件
        if (queryTask.getLeadingOfficialId() != null && !queryTask.getLeadingOfficialId().isEmpty()) {
            queryWrapper.like(Task::getLeadingOfficialId, queryTask.getLeadingOfficialId());
        }

//        queryWrapper.ne(Task::getStatus, 6);
//        queryWrapper.ne(Task::getStatus, 9);
        queryWrapper.eq(Task::getStatus, 2); //正常推进中

        // 处理leadingOfficialId模糊查询的情况
        if (deptDTOs != null && deptDTOs.size() > 0) {
            queryWrapper.and(wrapper -> {
                for (DeptDTO deptDTO : deptDTOs) {
                    wrapper.or(w -> w.like(Task::getLeadingDepartmentId, deptDTO.getDeptId()));
                }
            });
        } else if (queryTask.getUserId() != null && !queryTask.getUserId().isEmpty()) {
            // 使用apply方法添加复杂的OR条件
            queryWrapper.and(wrapper -> wrapper
                    .like(Task::getAssignerId, queryTask.getUserId())
                    .or()
                    .like(Task::getResponsiblePersonId, queryTask.getUserId())
            );
        }

        // 添加创建时间范围的筛选条件
        if (queryTask.getCreatedAtStart() != null && queryTask.getCreatedAtEnd() != null) {
            queryWrapper.between(Task::getCreatedAt, queryTask.getCreatedAtStart(), queryTask.getCreatedAtEnd());
        }

        return taskMapper.selectCount(queryWrapper);
    }

    @Override
    public Long countTasksOverdue(TaskSearchDTO queryTask, List<DeptDTO> deptDTOs) {
//        return taskMapper.countTasksOverdue(coOrganizerId, createdAtStart, createdAtEnd, leadingOfficialId);

        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();

//        queryWrapper.apply("updated_at > deadline");
        queryWrapper.gt(Task::getOverdueDays, 0); //当超时时间>0,并且status不为6或者9

        // 添加协办单位筛选条件
        if (queryTask.getCoOrganizerId() != null && !queryTask.getCoOrganizerId().isEmpty()) {
            queryWrapper.like(Task::getCoOrganizerId, queryTask.getCoOrganizerId());
        }
        if (queryTask.getLeadingDepartmentId() != null && !queryTask.getLeadingDepartmentId().isEmpty()) {
            queryWrapper.like(Task::getLeadingDepartmentId, queryTask.getLeadingDepartmentId());
        }
        if (queryTask.getSource() != null && !queryTask.getSource().isEmpty()) {
            queryWrapper.like(Task::getSource, queryTask.getSource());
        }

        // 添加牵头领导筛选条件
        if (queryTask.getLeadingOfficialId() != null && !queryTask.getLeadingOfficialId().isEmpty()) {
            queryWrapper.like(Task::getLeadingOfficialId, queryTask.getLeadingOfficialId());
        }

//        queryWrapper.and(wrapper -> wrapper
//                .ne(Task::getStatus, 6)
//                .or()
//                .ne(Task::getStatus, 9)
//        );
        queryWrapper.ne(Task::getStatus, 6);
        queryWrapper.ne(Task::getStatus, 9);
        // 处理leadingOfficialId模糊查询的情况
        if (deptDTOs != null && deptDTOs.size() > 0) {
            queryWrapper.and(wrapper -> {
                for (DeptDTO deptDTO : deptDTOs) {
                    wrapper.or(w -> w.like(Task::getLeadingDepartmentId, deptDTO.getDeptId()));
                }
            });
        } else if (queryTask.getUserId() != null && !queryTask.getUserId().isEmpty()) {
            // 使用apply方法添加复杂的OR条件
            queryWrapper.and(wrapper -> wrapper
                    .like(Task::getAssignerId, queryTask.getUserId())
                    .or()
                    .like(Task::getResponsiblePersonId, queryTask.getUserId())
            );
        }
        // 添加创建时间范围的筛选条件
        if (queryTask.getCreatedAtStart() != null && queryTask.getCreatedAtEnd() != null) {
            queryWrapper.between(Task::getCreatedAt, queryTask.getCreatedAtStart(), queryTask.getCreatedAtEnd());
        }

        return taskMapper.selectCount(queryWrapper);
    }

    @Override
    public Long countTasksComplete(TaskSearchDTO queryTask, List<DeptDTO> deptDTOs, Boolean taskPeriod) {
//        return taskMapper.countTasksComplete(coOrganizerId, createdAtStart, createdAtEnd, leadingOfficialId, taskPeriod);
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();

        // 添加 status 为 6 的条件
        queryWrapper.eq(Task::getStatus, queryTask.getStatus());
        // 添加协办单位筛选条件
        if (queryTask.getCoOrganizerId() != null && !queryTask.getCoOrganizerId().isEmpty()) {
            queryWrapper.like(Task::getCoOrganizerId, queryTask.getCoOrganizerId());
        }
        if (queryTask.getLeadingDepartmentId() != null && !queryTask.getLeadingDepartmentId().isEmpty()) {
            queryWrapper.like(Task::getLeadingDepartmentId, queryTask.getLeadingDepartmentId());
        }
        if (queryTask.getSource() != null && !queryTask.getSource().isEmpty()) {
            queryWrapper.like(Task::getSource, queryTask.getSource());
        }

        // 添加牵头领导筛选条件
        if (queryTask.getLeadingOfficialId() != null && !queryTask.getLeadingOfficialId().isEmpty()) {
            queryWrapper.like(Task::getLeadingOfficialId, queryTask.getLeadingOfficialId());
        }

        if (taskPeriod) {
            queryWrapper.eq(Task::getTaskPeriod, 1);
        }
        // 处理leadingOfficialId模糊查询的情况
        if (deptDTOs != null && deptDTOs.size() > 0) {
            queryWrapper.and(wrapper -> {
                for (DeptDTO deptDTO : deptDTOs) {
                    wrapper.or(w -> w.like(Task::getLeadingDepartmentId, deptDTO.getDeptId()));
                }
            });
        } else if (queryTask.getUserId() != null && !queryTask.getUserId().isEmpty()) {
            // 使用apply方法添加复杂的OR条件
            queryWrapper.and(wrapper -> wrapper
                    .like(Task::getAssignerId, queryTask.getUserId())
                    .or()
                    .like(Task::getResponsiblePersonId, queryTask.getUserId())
            );
        }
        // 添加创建时间范围的筛选条件
        if (queryTask.getCreatedAtStart() != null && queryTask.getCreatedAtEnd() != null) {
            queryWrapper.between(Task::getCreatedAt, queryTask.getCreatedAtStart(), queryTask.getCreatedAtEnd());
        }

        return taskMapper.selectCount(queryWrapper);
    }

    /**
     * 计算taskPeriod分别为1,2,3的任务总数。
     *
     * @return 符合条件的任务数量列表
     */
    @Override
    public List<Map<String, Object>> countTasksByTaskPeriod(TaskSearchDTO queryTask, List<String> leadingDepartmentIds) {
//        return taskMapper.countTasksByTaskPeriod(coOrganizerId, createdAtStart, createdAtEnd);
        return taskMapper.countTasksByTaskPeriod(leadingDepartmentIds, queryTask.getLeadingDepartmentId(), queryTask.getLeadingOfficialId(), queryTask.getSource(), queryTask.getCreatedAtStart(), queryTask.getCreatedAtEnd());
    }

    /**
     * 计算taskPeriod分别为1,2,3且状态status为6的任务数。
     *
     * @return 符合条件的任务数量列表
     */
    @Override
    public List<Map<String, Object>> countTasksByTaskPeriodAndStatus(TaskSearchDTO queryTask, List<String> leadingDepartmentIds) {
        return taskMapper.countTasksByTaskPeriodAndStatus(leadingDepartmentIds, queryTask.getLeadingDepartmentId(), queryTask.getLeadingOfficialId(), queryTask.getSource(), queryTask.getCreatedAtStart(), queryTask.getCreatedAtEnd());
    }

    /**
     * 计算根据fieldId分组的所有任务总数。
     *
     * @param queryTask
     * @param leadingDepartmentIds
     * @return
     */
    @Override
    public List<Map<String, Object>> countTasksByFieldId(TaskSearchDTO queryTask, List<String> leadingDepartmentIds) {
        return taskMapper.countTasksByFieldId(leadingDepartmentIds, queryTask.getLeadingDepartmentId(), queryTask.getLeadingOfficialId(), queryTask.getSource(), queryTask.getCreatedAtStart(), queryTask.getCreatedAtEnd());
    }

    /**
     * 计算根据fieldId分组且状态status为6的任务数。
     *
     * @param queryTask
     * @param leadingDepartmentIds
     * @return
     */
    @Override
    public List<Map<String, Object>> countTasksByFieldIdAndStatus(TaskSearchDTO queryTask, List<String> leadingDepartmentIds) {
        return taskMapper.countTasksByFieldIdAndStatus(leadingDepartmentIds, queryTask.getLeadingDepartmentId(), queryTask.getLeadingOfficialId(), queryTask.getSource(), queryTask.getCreatedAtStart(), queryTask.getCreatedAtEnd());
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

//    @Override
//    public List<String> getDistinctSources() {
//        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.select(Task::getSource).groupBy(Task::getSource);
//        return this.listObjs(queryWrapper, obj -> obj != null ? obj.toString() : null);
//    }

}