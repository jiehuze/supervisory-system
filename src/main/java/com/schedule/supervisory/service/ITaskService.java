package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.dto.DeptDTO;
import com.schedule.supervisory.dto.TaskSearchDTO;
import com.schedule.supervisory.entity.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ITaskService extends IService<Task> {

    /**
     * 插入任务
     *
     * @param task 要插入的任务对象
     */
    Long insertTask(Task task);

    /**
     * 批量插入任务
     *
     * @param tasks 要插入的任务对象列表
     */
    void batchInsertTasks(List<Task> tasks);

    /**
     * 更新任务信息
     *
     * @param task 要更新的任务对象
     */
    void updateTask(Task task);

    /**
     * 获取任务列表
     *
     * @return 任务列表
     */
    List<Task> listTasks();

    List<String> getDistinctSources();

    /**
     * 根据任务状态获取任务列表
     *
     * @param status 任务状态
     * @return 符合条件的任务列表
     */
    List<Task> listTasksByStatus(Integer status);

    IPage<Task> getTasksByConditions(TaskSearchDTO queryTask, int pageNum, int pageSize, List<DeptDTO> deptDTOs);

    boolean updateStatusById(Long taskId, Integer newStatus);

    boolean updateIsUrgentById(Long taskId, Boolean isUrgent);

    boolean updateInstructionById(Long taskId, String instrunction);

    public Task getTaskById(Long id);

    boolean updateCbApplyDone(Task task);

    boolean updateClosureReview(Task task);

    boolean updateCancelInfo(Task task);

    //承办人填报
    boolean updateCbReport(Task task);

    //计算总数
    Long countTasksNums(LocalDateTime createdAtStart, LocalDateTime createdAtEnd, String coOrganizerId, String leadingOfficialId);

    List<Map<String, Object>> getStatusStatistics(LocalDateTime createdAtStart, LocalDateTime createdAtEnd, String coOrganizerId);

    //获取状态为已完成，并未超期任务数
    Long countTasksCompleteOnTime(LocalDateTime createdAtStart, LocalDateTime createdAtEnd, String coOrganizerId, String leadingOfficialId);

    Long countTasksInProgress(LocalDateTime createdAtStart, LocalDateTime createdAtEnd, String coOrganizerId, String leadingOfficialId);

    Long countTasksOverdue(LocalDateTime createdAtStart, LocalDateTime createdAtEnd, String coOrganizerId, String leadingOfficialId);

    Long countTasksComplete(LocalDateTime createdAtStart, LocalDateTime createdAtEnd, String coOrganizerId, String leadingOfficialId, Boolean taskPeriod);

    //根据任务周期读取已办结数量
    List<Map<String, Object>> countTasksByTaskPeriod(String coOrganizerId, LocalDateTime createdAtStart, LocalDateTime createdAtEnd);

    List<Map<String, Object>> countTasksByTaskPeriodAndStatus(String coOrganizerId, LocalDateTime createdAtStart, LocalDateTime createdAtEnd);

    //根据所属领域读取已办结数量
    List<Map<String, Object>> countTasksByFieldId(String coOrganizerId, LocalDateTime createdAtStart, LocalDateTime createdAtEnd);

    List<Map<String, Object>> countTasksByFieldIdAndStatus(String coOrganizerId, LocalDateTime createdAtStart, LocalDateTime createdAtEnd);

    //更新超期任务
    void updateOverdueDays();

    public List<Task> getTasksDueInHours(int hours);
}