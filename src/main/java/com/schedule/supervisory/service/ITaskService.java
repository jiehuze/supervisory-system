package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.dto.TaskDTO;
import com.schedule.supervisory.entity.Task;

import java.util.List;

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

    IPage<Task> getTasksByConditions(Task queryTask, int pageNum, int pageSize);

    boolean updateStatusById(Long taskId, Integer newStatus);

    boolean updateIsUrgentById(Long taskId, Boolean isUrgent);

    boolean updateInstructionById(Long taskId, String instrunction);

    public Task getTaskById(Long id);

    boolean updateCbApplyDone(Task task);

    boolean updateClosureReview(Task task);

    boolean updateCancelInfo(Task task);

    //承办人填报
    boolean updateCbReport(Task task);
}