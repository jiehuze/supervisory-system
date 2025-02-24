package com.schedule.supervisory.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schedule.supervisory.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    @Select("SELECT DISTINCT source FROM public.task ORDER BY source")
    List<String> selectDistinctSources();

    @Select("<script>" +
            "WITH status_counts AS (" +
            "SELECT status, COUNT(*) AS count " +
            "FROM task " +
            "<where>" +
            "<if test='createdAtStart != null'> AND created_at &gt;= #{createdAtStart} </if>" +
            "<if test='createdAtEnd != null'> AND created_at &lt;= #{createdAtEnd} </if>" +
            "<if test='coOrganizerId != null and coOrganizerId != \"\"'> AND co_organizer_id LIKE CONCAT('%', #{coOrganizerId}, '%') </if>" +
            "</where>" +
            "AND status IN (2, 3, 6) " +
            "GROUP BY status" +
            ") " +
            "SELECT status, count AS status_count, (SELECT SUM(count) FROM status_counts) AS total_task_count FROM status_counts" +
            "</script>")
    List<Map<String, Object>> getStatusStatistics(
            @Param("createdAtStart") LocalDateTime createdAtStart,
            @Param("createdAtEnd") LocalDateTime createdAtEnd,
            @Param("coOrganizerId") String coOrganizerId);

    //推进中的任务:推进中：系统中所有未办结和取消的任务
    @Select("<script>" +
            "SELECT COUNT(*) AS count FROM task " +
            "WHERE status != 6 AND status != 9 " +
            "<if test='coOrganizerId != null and coOrganizerId != \"\"'> AND co_organizer_id LIKE CONCAT('%', #{coOrganizerId}, '%')</if>" +
            "<if test='createdAtStart != null and createdAtEnd != null'> AND created_at BETWEEN #{createdAtStart} AND #{createdAtEnd}</if>" +
            "<if test='leadingOfficialId != null and leadingOfficialId != \"\"'> AND leading_official_id LIKE CONCAT('%', #{leadingOfficialId}, '%')</if>" + //牵头区领导id查询
            "</script>")
    long countTasksInProgress(@Param("coOrganizerId") String coOrganizerId,
                              @Param("createdAtStart") LocalDateTime createdAtStart,
                              @Param("createdAtEnd") LocalDateTime createdAtEnd,
                              @Param("leadingOfficialId") String leadingOfficialId);

    //办结任务：状态为已办结的任务
    @Select("<script>" +
            "SELECT COUNT(*) AS count FROM task " +
            "WHERE status = 6 " +
            "<if test='coOrganizerId != null and coOrganizerId != \"\"'> AND co_organizer_id LIKE CONCAT('%', #{coOrganizerId}, '%')</if>" +
            "<if test='createdAtStart != null and createdAtEnd != null'> AND created_at BETWEEN #{createdAtStart} AND #{createdAtEnd}</if>" +
            "<if test='taskPeriod == true'> AND task_period = 1</if>" +
            "<if test='leadingOfficialId != null and leadingOfficialId != \"\"'> AND leading_official_id LIKE CONCAT('%', #{leadingOfficialId}, '%')</if>" + //牵头区领导id查询
            "</script>")
    long countTasksComplete(@Param("coOrganizerId") String coOrganizerId,
                            @Param("createdAtStart") LocalDateTime createdAtStart,
                            @Param("createdAtEnd") LocalDateTime createdAtEnd,
                            @Param("leadingOfficialId") String leadingOfficialId,
                            @Param("taskPeriod") Boolean taskPeriod);

    //逾期任务：任务状态为逾期的，（当前时间大于预期办结时间）,转态不是完成和取消
    @Select("<script>" +
            "SELECT COUNT(*) AS count FROM task " +
            "WHERE status != 6 OR status != 9 and updated_at > deadline " +
            "<if test='coOrganizerId != null and coOrganizerId != \"\"'> AND co_organizer_id LIKE CONCAT('%', #{coOrganizerId}, '%')</if>" +
            "<if test='createdAtStart != null and createdAtEnd != null'> AND created_at BETWEEN #{createdAtStart} AND #{createdAtEnd}</if>" +
            "<if test='leadingOfficialId != null and leadingOfficialId != \"\"'> AND leading_official_id LIKE CONCAT('%', #{leadingOfficialId}, '%')</if>" + //牵头区领导id查询
            "</script>")
    long countTasksOverdue(@Param("coOrganizerId") String coOrganizerId,
                           @Param("createdAtStart") LocalDateTime createdAtStart,
                           @Param("createdAtEnd") LocalDateTime createdAtEnd,
                           @Param("leadingOfficialId") String leadingOfficialId);

//    @Select("<script>" +
//            "SELECT task_period, COUNT(*) AS count FROM task " +
//            "WHERE task_period IN (1, 2, 3) " +
//            "<if test='coOrganizerId != null and coOrganizerId != \"\"'> AND co_organizer_id LIKE CONCAT('%', #{coOrganizerId}, '%')</if>" +
//            "<if test='createdAtStart != null and createdAtEnd != null'> AND created_at BETWEEN #{createdAtStart} AND #{createdAtEnd}</if>" +
//            "GROUP BY task_period" +
//            "</script>")
//    List<Map<String, Object>> countTasksByTaskPeriod(@Param("coOrganizerId") String coOrganizerId,
//                                                     @Param("createdAtStart") LocalDateTime createdAtStart,
//                                                     @Param("createdAtEnd") LocalDateTime createdAtEnd);

    @Select("<script>" +
            "SELECT task_period, COUNT(*) AS count FROM task " +
            "WHERE task_period IN (1, 2, 3, 4) " +
            "<if test='leadingDepartmentId != null and leadingDepartmentId != \"\"'> AND leading_department_id LIKE CONCAT('%', #{leadingDepartmentId}, '%')</if>" +
            "<if test='leadingDepartmentIds != null and !leadingDepartmentIds.isEmpty()'> AND ( " +
            "<foreach item='id' collection='leadingDepartmentIds' separator=' OR ' open='' close=''>" +
            "leading_department_id LIKE CONCAT('%', #{id}, '%')" +
            "</foreach>" +
            ") </if>" +
            "<if test='leadingOfficialId != null and leadingOfficialId != \"\"'> AND leading_official_id LIKE CONCAT('%', #{leadingOfficialId}, '%')</if>" +
            "<if test='source != null and source != \"\"'> AND source LIKE CONCAT('%', #{source}, '%')</if>" +
            "<if test='createdAtStart != null and createdAtEnd != null'> AND created_at BETWEEN #{createdAtStart} AND #{createdAtEnd}</if>" +
            "GROUP BY task_period" +
            "</script>")
    List<Map<String, Object>> countTasksByTaskPeriod(@Param("leadingDepartmentIds") List<String> leadingDepartmentIds,
                                                     @Param("leadingDepartmentId") String leadingDepartmentId,
                                                     @Param("leadingOfficialId") String leadingOfficialId,
                                                     @Param("source") String source,
                                                     @Param("createdAtStart") LocalDateTime createdAtStart,
                                                     @Param("createdAtEnd") LocalDateTime createdAtEnd);

//    @Select("<script>" +
//            "SELECT task_period, COUNT(*) AS count FROM task " +
//            "WHERE status = 6 AND task_period IN (1, 2, 3) " +
//            "<if test='coOrganizerId != null and coOrganizerId != \"\"'> AND co_organizer_id LIKE CONCAT('%', #{coOrganizerId}, '%')</if>" +
//            "<if test='createdAtStart != null and createdAtEnd != null'> AND created_at BETWEEN #{createdAtStart} AND #{createdAtEnd}</if>" +
//            "GROUP BY task_period" +
//            "</script>")
//    List<Map<String, Object>> countTasksByTaskPeriodAndStatus(@Param("coOrganizerId") String coOrganizerId,
//                                                              @Param("createdAtStart") LocalDateTime createdAtStart,
//                                                              @Param("createdAtEnd") LocalDateTime createdAtEnd);

    @Select("<script>" +
            "SELECT task_period, COUNT(*) AS count FROM task " +
            "WHERE status = 6 AND task_period IN (1, 2, 3, 4) " +
            "<if test='leadingDepartmentId != null and leadingDepartmentId != \"\"'> AND leading_department_id LIKE CONCAT('%', #{leadingDepartmentId}, '%')</if>" +
            "<if test='leadingDepartmentIds != null and !leadingDepartmentIds.isEmpty()'> AND ( " +
            "<foreach item='id' collection='leadingDepartmentIds' separator=' OR ' open='' close=''>" +
            "leading_department_id LIKE CONCAT('%', #{id}, '%')" +
            "</foreach>" +
            ") </if>" +
            "<if test='leadingOfficialId != null and leadingOfficialId != \"\"'> AND leading_official_id LIKE CONCAT('%', #{leadingOfficialId}, '%')</if>" +
            "<if test='source != null and source != \"\"'> AND source LIKE CONCAT('%', #{source}, '%')</if>" +
            "<if test='createdAtStart != null and createdAtEnd != null'> AND created_at BETWEEN #{createdAtStart} AND #{createdAtEnd}</if>" +
            "GROUP BY task_period" +
            "</script>")
    List<Map<String, Object>> countTasksByTaskPeriodAndStatus(@Param("leadingDepartmentIds") List<String> leadingDepartmentIds,
                                                              @Param("leadingDepartmentId") String leadingDepartmentId,
                                                              @Param("leadingOfficialId") String leadingOfficialId,
                                                              @Param("source") String source,
                                                              @Param("createdAtStart") LocalDateTime createdAtStart,
                                                              @Param("createdAtEnd") LocalDateTime createdAtEnd);

//    @Select("<script>" +
//            "SELECT field_id, COUNT(*) AS count FROM task " +
//            "<where>" +
//            "<if test='coOrganizerId != null and coOrganizerId != \"\"'> AND co_organizer_id LIKE CONCAT('%', #{coOrganizerId}, '%')</if>" +
//            "<if test='createdAtStart != null and createdAtEnd != null'> AND created_at BETWEEN #{createdAtStart} AND #{createdAtEnd}</if>" +
//            "</where>" +
//            "GROUP BY field_id" +
//            "</script>")
//    List<Map<String, Object>> countTasksByFieldId(@Param("coOrganizerId") String coOrganizerId,
//                                                  @Param("createdAtStart") LocalDateTime createdAtStart,
//                                                  @Param("createdAtEnd") LocalDateTime createdAtEnd);

    @Select("<script>" +
            "SELECT field_id, COUNT(*) AS count FROM task " +
            "<where>" +
            "<if test='leadingDepartmentId != null and leadingDepartmentId != \"\"'> AND leading_department_id LIKE CONCAT('%', #{leadingDepartmentId}, '%')</if>" +
            "<if test='leadingDepartmentIds != null and !leadingDepartmentIds.isEmpty()'> AND ( " +
            "<foreach item='id' collection='leadingDepartmentIds' separator=' OR ' open='' close=''>" +
            "leading_department_id LIKE CONCAT('%', #{id}, '%')" +
            "</foreach>" +
            ") </if>" +
            "<if test='leadingOfficialId != null and leadingOfficialId != \"\"'> AND leading_official_id LIKE CONCAT('%', #{leadingOfficialId}, '%')</if>" +
            "<if test='source != null and source != \"\"'> AND source LIKE CONCAT('%', #{source}, '%')</if>" +
            "<if test='createdAtStart != null and createdAtEnd != null'> AND created_at BETWEEN #{createdAtStart} AND #{createdAtEnd}</if>" +
            "</where>" +
            "GROUP BY field_id" +
            "</script>")
    List<Map<String, Object>> countTasksByFieldId(@Param("leadingDepartmentIds") List<String> leadingDepartmentIds,
                                                  @Param("leadingDepartmentId") String leadingDepartmentId,
                                                  @Param("leadingOfficialId") String leadingOfficialId,
                                                  @Param("source") String source,
                                                  @Param("createdAtStart") LocalDateTime createdAtStart,
                                                  @Param("createdAtEnd") LocalDateTime createdAtEnd);

//    @Select("<script>" +
//            "SELECT field_id, COUNT(*) AS count FROM task " +
//            "WHERE status = 6" +
//            "<if test='coOrganizerId != null and coOrganizerId != \"\"'> AND co_organizer_id LIKE CONCAT('%', #{coOrganizerId}, '%')</if>" +
//            "<if test='createdAtStart != null and createdAtEnd != null'> AND created_at BETWEEN #{createdAtStart} AND #{createdAtEnd}</if>" +
//            "GROUP BY field_id" +
//            "</script>")
//    List<Map<String, Object>> countTasksByFieldIdAndStatus(@Param("coOrganizerId") String coOrganizerId,
//                                                           @Param("createdAtStart") LocalDateTime createdAtStart,
//                                                           @Param("createdAtEnd") LocalDateTime createdAtEnd);

    @Select("<script>" +
            "SELECT field_id, COUNT(*) AS count FROM task " +
            "WHERE status = 6" +
            "<if test='leadingDepartmentId != null and leadingDepartmentId != \"\"'> AND leading_department_id LIKE CONCAT('%', #{leadingDepartmentId}, '%')</if>" +
            "<if test='leadingDepartmentIds != null and !leadingDepartmentIds.isEmpty()'> AND ( " +
            "<foreach item='id' collection='leadingDepartmentIds' separator=' OR ' open='' close=''>" +
            "leading_department_id LIKE CONCAT('%', #{id}, '%')" +
            "</foreach>" +
            ") </if>" +
            "<if test='leadingOfficialId != null and leadingOfficialId != \"\"'> AND leading_official_id LIKE CONCAT('%', #{leadingOfficialId}, '%')</if>" +
            "<if test='source != null and source != \"\"'> AND source LIKE CONCAT('%', #{source}, '%')</if>" +
            "<if test='createdAtStart != null and createdAtEnd != null'> AND created_at BETWEEN #{createdAtStart} AND #{createdAtEnd}</if>" +
            "GROUP BY field_id" +
            "</script>")
    List<Map<String, Object>> countTasksByFieldIdAndStatus(@Param("leadingDepartmentIds") List<String> leadingDepartmentIds,
                                                           @Param("leadingDepartmentId") String leadingDepartmentId,
                                                           @Param("leadingOfficialId") String leadingOfficialId,
                                                           @Param("source") String source,
                                                           @Param("createdAtStart") LocalDateTime createdAtStart,
                                                           @Param("createdAtEnd") LocalDateTime createdAtEnd);

//    @Update("UPDATE task SET overdue_days = EXTRACT(DAY FROM (CURRENT_DATE - deadline)) " +
//            "WHERE status = 3 AND deadline < CURRENT_DATE")
//    void updateOverdueDays(@Param("id") int id);

    // 更新状态不为3但是超过截止日期的任务状态为3
    @Update("UPDATE task SET status = 3 WHERE status != 3 AND deadline < CURRENT_DATE")
    void updateStatusForOverdueTasks();

    // 计算并更新所有状态为3的任务的超期天数
//    @Update("UPDATE task SET overdue_days = EXTRACT(DAY FROM (CURRENT_DATE - deadline)) " +
//            "WHERE status = 3")
//    void updateOverdueDays();

    //计算状态为6已完成时，没有超期的任务数
    @Select("SELECT COUNT(*) " +
            "FROM task " +
            "WHERE status = 6 AND updated_at <= deadline")
    int countTasksCompleteOmTime();

//    @Update("UPDATE task " +
//            "SET overdue_days = GREATEST((CURRENT_DATE - deadline), 0) " +
//            "WHERE status NOT IN (6, 9) " +
//            "AND updated_at > deadline " +
//            "AND deadline IS NOT NULL")
//    void updateOverdueDays();

    @Update("UPDATE task " +
            "SET overdue_days = GREATEST((CURRENT_DATE - deadline), 0) " +
            "WHERE status NOT IN (6, 9) " +
            "AND CURRENT_DATE > deadline " + // 修改这里使用CURRENT_DATE代替updated_at
            "AND deadline IS NOT NULL")
    void updateOverdueDays();
}