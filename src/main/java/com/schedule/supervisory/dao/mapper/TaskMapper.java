package com.schedule.supervisory.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schedule.supervisory.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {

    /**
     * 插入任务
     *
     * @param task 要插入的任务对象
     */
    void insertTask(Task task);

    /**
     * 批量插入任务
     *
     * @param tasks 要插入的任务对象列表
     */
    void batchInsertTasks(@Param("tasks") List<Task> tasks);

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

    /**
     * 根据任务状态获取任务列表
     *
     * @param status 任务状态
     * @return 符合条件的任务列表
     */
    List<Task> listTasksByStatus(@Param("status") Integer status);
}