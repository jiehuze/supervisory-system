package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.dto.DeptDTO;
import com.schedule.supervisory.dto.ProcessCheckInfoDTO;
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

    void updateCheckProcess(Long taskId, String processInstanceId, String processInstanceReportId, String processInstanceReviewIds);

    //办结描述
    void updateCheckDoneDesc(Long taskId, String doneDesc);

    //终止描述
    void updateCheckCancelDesc(Long taskId, String cancelDesc);

    /**
     * 获取任务列表
     *
     * @return 任务列表
     */
    List<Task> listTasks();

    List<String> getDistinctSources(String source);

    /**
     * 根据任务状态获取任务列表
     *
     * @param status 任务状态
     * @return 符合条件的任务列表
     */
    List<Task> listTasksByStatus(Integer status);

    List<Task> listTasksBySource(String source);

    List<Task> ListTasksOverdue();

    List<Task> ListTasksCountDown();
    List<Task> ListTasksUnfilled();

    IPage<Task> queryTasksByConditions(TaskSearchDTO queryTask, int pageNum, int pageSize, List<DeptDTO> deptDTOs);

    IPage<Task> getTasksByConditions(TaskSearchDTO queryTask, int pageNum, int pageSize, List<DeptDTO> deptDTOs);

    List<Task> getTasksBySearchDTO(TaskSearchDTO queryTask);

    List<Task> getTasksByIds(List<Integer> ids);

    boolean updateStatusById(Long taskId, Integer newStatus);

    boolean updateIsUrgentById(Long taskId, Boolean isUrgent);

    boolean updateCheckById(Long taskId, Integer addStatus, Integer removeStatus);

    boolean adminCheckById(Long taskId, Integer status, String userId);

    boolean adminDeleteById(Long taskId, String userId);

    boolean updateCheckInfoById(Long taskId, ProcessCheckInfoDTO processCheckInfoDTO);

    boolean clearCheckUserById(Long taskId);

    boolean updateInstructionById(Long taskId, String instrunction);

    public Task getTaskById(Long id);

    boolean updateCbApplyDone(Task task);

    boolean updateClosureReview(Task task);

    boolean updateCancelInfo(Task task);

    //承办人填报
    boolean updateCbReport(Task task);

    //计算总数
    Long countTasksNums(TaskSearchDTO queryTask, List<DeptDTO> deptDTOs);

    List<Map<String, Object>> getStatusStatistics(LocalDateTime createdAtStart, LocalDateTime createdAtEnd, String coOrganizerId);

    //获取状态为已完成，并未超期任务数
    Long countTasksCompleteOnTime(TaskSearchDTO queryTask, List<DeptDTO> deptDTOs);

    Long countTasksInProgress(TaskSearchDTO queryTask, List<DeptDTO> deptDTOs);

    Long countTasksOverdue(TaskSearchDTO queryTask, List<DeptDTO> deptDTOs);

    Long countTasksComplete(TaskSearchDTO queryTask, List<DeptDTO> deptDTOs, Boolean taskPeriod);

    //根据任务周期读取已办结数量
    List<Map<String, Object>> countTasksByTaskPeriod(TaskSearchDTO queryTask, List<String> leadingDepartmentIds);

    List<Map<String, Object>> countTasksByTaskPeriod2(TaskSearchDTO queryTask, List<DeptDTO> deptDTOs);

    List<Map<String, Object>> countTasksByTaskPeriodAndStatus(TaskSearchDTO queryTask, List<String> leadingDepartmentIds);

    List<Map<String, Object>> countTasksByTaskPeriodAndStatus2(TaskSearchDTO queryTask, List<DeptDTO> deptDTOs);

    //根据所属领域读取已办结数量
    List<Map<String, Object>> countTasksByFieldId(TaskSearchDTO queryTask, List<String> leadingDepartmentIds);

    List<Map<String, Object>> countTasksByFieldId2(TaskSearchDTO queryTask, List<DeptDTO> deptDTOs);

    List<Map<String, Object>> countTasksByFieldId3(TaskSearchDTO queryTask, List<DeptDTO> deptDTOs);

    List<Map<String, Object>> countTasksByFieldIdAndStatus(TaskSearchDTO queryTask, List<String> leadingDepartmentIds);

    List<Map<String, Object>> countTasksByFieldIdAndStatus2(TaskSearchDTO queryTask, List<DeptDTO> deptDTOs);

    List<Map<String, Object>> countTasksByFieldIdAndStatus3(TaskSearchDTO queryTask, List<DeptDTO> deptDTOs);

    //更新超期任务
    void updateOverdueDays();

    void updateCountDownDays();

    Long countCountDownDays();

    int updateIsFilled();

    boolean updateOverdueDays(Long taskId, int days);

    public List<Task> getTasksDueInHours(int hours);

    //获取去重的source
//    public List<String> getDistinctSources();
}