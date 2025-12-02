package com.schedule.supervisory.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.supervisory.dto.DeptDTO;
import com.schedule.supervisory.dto.TaskSearchDTO;
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

//    @Select("SELECT DISTINCT source FROM public.task ORDER BY source")
//    List<String> selectDistinctSources();

    @Select("SELECT DISTINCT source " +
            "FROM public.task " +
            "WHERE source LIKE CONCAT('%', #{source}, '%') " +
            "AND delete = false " +  // 添加delete=false条件
            "ORDER BY source")
    List<String> selectDistinctSources(@Param("source") String source);

    /**
     * 自定义分页查询方法
     */
    @Select("<script>" +
            "SELECT *, " +
            "CASE WHEN status = 6 THEN 0 WHEN status = 9 Then 0 WHEN overdue_days > 0 THEN 1 ELSE 0 END AS overdue_days1, " +
            "CASE WHEN status = 9 THEN 2 WHEN status = 6 THEN 1 ELSE 3 END AS order_status " +
            "FROM task " +
            "<where>" + // 使用<where>标签代替WHERE 1=1
            "<if test='queryTask.taskId != null'> AND id = #{queryTask.taskId}</if>" +
            "AND delete = false " +  // 添加delete=false条件
            "<if test='queryTask.taskType != null'> AND task_type = #{queryTask.taskType}</if>" +
            "<if test='queryTask.fieldId != null'> AND field_id = #{queryTask.fieldId}</if>" +
            "<if test='queryTask.isFilled != null'> AND is_filled = #{queryTask.isFilled}</if>" +
            // 优化后的 firstFieldId 查询
            "<if test='queryTask.firstFieldIds != null and !queryTask.firstFieldIds.isEmpty() " +
            "         or queryTask.secondFieldIds != null and !queryTask.secondFieldIds.isEmpty() " +
            "         or queryTask.thirdFieldIds != null and !queryTask.thirdFieldIds.isEmpty()'>" +
            "AND( " +
            "<if test='queryTask.firstFieldIds != null and !queryTask.firstFieldIds.isEmpty()'> " +
            "EXISTS ( " +
            "    SELECT 1 " +
            "    FROM unnest(string_to_array(field_ids, ',')) AS field_first_id " +
            "    WHERE field_first_id = ANY(string_to_array(#{queryTask.firstFieldIds}, ',')::text[]) " +
            ") " +
            "</if>" +
            "<if test='queryTask.firstFieldIds != null and !queryTask.firstFieldIds.isEmpty() " +
            "  and queryTask.secondFieldIds != null and !queryTask.secondFieldIds.isEmpty()'>\n" +
            "  OR " +
            "</if>" +
            // 第二个字段改为数组查询
            "<if test='queryTask.secondFieldIds != null and !queryTask.secondFieldIds.isEmpty()'> " +
            "EXISTS ( " +
            "    SELECT 1 " +
            "    FROM unnest(string_to_array(field_second_ids, ',')) AS field_second_id " +
            "    WHERE field_second_id = ANY(string_to_array(#{queryTask.secondFieldIds}, ',')::text[]) " +
            ") " +
            "</if>" +
            "<if test='(queryTask.firstFieldIds != null and !queryTask.firstFieldIds.isEmpty() " +
            "  or queryTask.secondFieldIds != null and !queryTask.secondFieldIds.isEmpty()) " +
            "  and queryTask.thirdFieldIds != null and !queryTask.thirdFieldIds.isEmpty()'> " +
            "  AND " +
            "</if> " +
            // 优化后的 thirdFieldId 查询
            "<if test='queryTask.thirdFieldIds != null and !queryTask.thirdFieldIds.isEmpty()'> " +
            "EXISTS ( " +
            "    SELECT 1 " +
            "    FROM unnest(string_to_array(field_third_ids, ',')) AS field_third_id " +
            "    WHERE field_third_id = ANY(string_to_array(#{queryTask.thirdFieldIds}, ',')::text[]) " +
            ") " +
            "</if>" +
            ") </if>" +
            "<if test='queryTask.source != null and queryTask.source != \"\"'> AND source LIKE CONCAT('%', #{queryTask.source}, '%')</if>" +
            "<if test='queryTask.content != null and queryTask.content != \"\"'> AND content LIKE CONCAT('%', #{queryTask.content}, '%')</if>" +
            "<if test='queryTask.leadingOfficial != null and queryTask.leadingOfficial != \"\"'> AND leading_official LIKE CONCAT('%', #{queryTask.leadingOfficial}, '%')</if>" +
            "<if test='queryTask.assignerId != null and queryTask.assignerId != \"\"'> AND assigner_id LIKE CONCAT('%', #{queryTask.assignerId}, '%')</if>" +
            "<if test='queryTask.leadingOfficialId != null and queryTask.leadingOfficialId != \"\"'> AND leading_official_id LIKE CONCAT('%', #{queryTask.leadingOfficialId}, '%')</if>" +
            "<if test='queryTask.unAuth == null or !queryTask.unAuth'> AND (" +
            "<foreach collection='deptDTOs' item='dept' separator=' OR '> " +
            "(leading_department_id LIKE CONCAT('%', #{dept.deptId}, '%') OR co_organizer_id LIKE CONCAT('%', #{dept.deptId}, '%')) " +
            "</foreach> " +
            "<if test='queryTask.userId != null and queryTask.userId != \"\"'> " +
            "<if test='deptDTOs == null or deptDTOs.isEmpty()'>" + // 如果deptDTOs为空，则不包括OR前缀
            " (assigner_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR responsible_person_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR undertaker_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR leading_official_id LIKE CONCAT('%', #{queryTask.userId}, '%')) " +
            "</if>" +
            "<if test='deptDTOs != null and !deptDTOs.isEmpty()'>" + // 检查deptDTOs是否非空
            "OR (assigner_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR responsible_person_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR undertaker_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR leading_official_id LIKE CONCAT('%', #{queryTask.userId}, '%')) " +
            "</if>" +
            "</if>" +
            ") </if>" +
            "<if test='queryTask.leadingDepartmentId != null and queryTask.leadingDepartmentId != \"\"'> AND leading_department_id LIKE CONCAT('%', #{queryTask.leadingDepartmentId}, '%')</if>" +
            "<if test='queryTask.responsiblePersonId != null and queryTask.responsiblePersonId != \"\"'> AND responsible_person_id LIKE CONCAT('%', #{queryTask.responsiblePersonId}, '%')</if>" +
            "<if test='queryTask.deadline != null'> AND deadline BETWEEN '2020-01-01' AND #{queryTask.deadline}</if>" +  // 修改为大于等于
            "<if test='queryTask.taskPeriod != null'> AND task_period = #{queryTask.taskPeriod}</if>" +
            "<if test='queryTask.status != null'>" +
            "<choose>" +
            "<when test='queryTask.status == 3'> AND status NOT IN (6, 9) AND overdue_days > 0</when>" +
            "<otherwise> AND status = #{queryTask.status}</otherwise>" +
            "</choose>" +
            "</if>" +
            "<if test='queryTask.unfinished != null and queryTask.unfinished'> AND status NOT IN (6, 9)</if>" +
            "<if test='queryTask.overDue != null'>\n" +
            "  <choose>" +
            "    <when test='queryTask.overDue'>" +
            "      AND overdue_days IS NOT NULL AND overdue_days > 0" +
            "    </when>" +
            "    <otherwise>\n" +
            "      AND (overdue_days IS NULL OR overdue_days = 0)" +
            "    </otherwise>" +
            "  </choose>" +
            "</if>" +
            "<if test='queryTask.countDown != null and queryTask.countDown'> AND count_down_days  IS NOT NULL AND count_down_days > 0 </if>" +
            "<if test='queryTask.untreated != null and queryTask.untreated'>" +
            " AND (" +
            "     (status = 12 AND process_instance_review_ids LIKE CONCAT('%', #{queryTask.userId}, '%'))" +
            "     <if test='queryTask.accept != null and queryTask.accept'>" +
            "         OR (status = 1 " +
            "<if test='deptDTOs != null and deptDTOs.size() > 0'>" +
            " AND ( " +
            "<foreach collection='deptDTOs' item='dept' separator=' OR '> " +
            "(leading_department_id LIKE CONCAT('%', #{dept.deptId}, '%')) " +
            "</foreach> " +
            " ) " +
            "</if>" +
            " ) " +
            "     </if>" +
            " )" +
            "</if>" +
            "<if test='queryTask.createdAtStart != null and queryTask.createdAtEnd != null'> AND created_at BETWEEN #{queryTask.createdAtStart} AND #{queryTask.createdAtEnd}</if>" +
            "</where>" +
            "ORDER BY " +
            "<choose>" +
            "<when test='queryTask.sourceOrder != null'>source ${queryTask.sourceOrder}</when>" +
            "<when test='queryTask.deadlineOrder != null'>deadline ${queryTask.deadlineOrder}</when>" +
            "<when test='queryTask.statusOrder != null'>status ${queryTask.statusOrder}</when>" +
            "<when test='queryTask.leadingOfficialOrder != null'>leading_official_order ${queryTask.leadingOfficialOrder}</when>" +
            "<when test='queryTask.leadingDepartmentOrder != null'>leading_department_order ${queryTask.leadingDepartmentOrder}</when>" +
            "<otherwise>" +
            "<choose>" +
            "<when test='queryTask.systemAppType == \"gov\"'>overdue_days1 DESC, created_at DESC</when>" +
            "<otherwise>overdue_days1 DESC, order_status DESC, source_date DESC</otherwise>" +
            "</choose>" +
            "</otherwise>" +
            "</choose>" +
            "</script>")
    Page<Task> selectTasks(Page<Task> page, @Param("queryTask") TaskSearchDTO queryTask, @Param("deptDTOs") List<DeptDTO> deptDTOs);


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
            "AND delete = false " +  // 添加delete=false条件
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
            "AND delete = false " +  // 添加delete=false条件
            "<if test='leadingDepartmentId != null and leadingDepartmentId != \"\"'> AND leading_department_id LIKE CONCAT('%', #{leadingDepartmentId}, '%')</if>" +
            "<if test='leadingDepartmentIds != null and !leadingDepartmentIds.isEmpty()'> AND ( " +
            "<foreach item='id' collection='leadingDepartmentIds' separator=' OR ' open='' close=''>" +
            "(leading_department_id LIKE CONCAT('%', #{id}, '%') ) " +
            "</foreach>" +
            ") </if>" +
            "<if test='leadingOfficialId != null and leadingOfficialId != \"\"'> AND leading_official_id LIKE CONCAT('%', #{leadingOfficialId}, '%')</if>" +
            "<if test='source != null and source != \"\"'> AND source LIKE CONCAT('%', #{source}, '%')</if>" +
            "<if test='phoneUsed != null and phoneUsed'> AND status != 9</if>" +
            "<if test='createdAtStart != null and createdAtEnd != null'> AND created_at BETWEEN #{createdAtStart} AND #{createdAtEnd}</if>" +
            "GROUP BY task_period" +
            "</script>")
    List<Map<String, Object>> countTasksByTaskPeriod(@Param("leadingDepartmentIds") List<String> leadingDepartmentIds,
                                                     @Param("leadingDepartmentId") String leadingDepartmentId,
                                                     @Param("leadingOfficialId") String leadingOfficialId,
                                                     @Param("source") String source,
                                                     @Param("phoneUsed") Boolean phoneUsed,
                                                     @Param("createdAtStart") LocalDateTime createdAtStart,
                                                     @Param("createdAtEnd") LocalDateTime createdAtEnd);

    @Select("<script>" +
            "SELECT task_period, COUNT(*) AS count FROM task " +
            "<where>" + // 使用<where>标签代替WHERE 1=1
            "task_period IN (1, 2, 3, 4) " +
            "AND delete = false " +  // 添加delete=false条件
            "<if test='queryTask.taskType != null'> AND task_type = #{queryTask.taskType}</if>" +
            "<if test='queryTask.source != null and queryTask.source != \"\"'> AND source LIKE CONCAT('%', #{queryTask.source}, '%')</if>" +
            "<if test='queryTask.assignerId != null and queryTask.assignerId != \"\"'> AND assigner_id LIKE CONCAT('%', #{queryTask.assignerId}, '%')</if>" +
            "<if test='queryTask.leadingOfficialId != null and queryTask.leadingOfficialId != \"\"'> AND leading_official_id LIKE CONCAT('%', #{queryTask.leadingOfficialId}, '%')</if>" +
            "<if test='queryTask.unAuth == null or !queryTask.unAuth'> AND (" +
            "<foreach collection='deptDTOs' item='dept' separator=' OR '> " +
            "(leading_department_id LIKE CONCAT('%', #{dept.deptId}, '%') OR co_organizer_id LIKE CONCAT('%', #{dept.deptId}, '%')) " +
            "</foreach> " +
            "<if test='queryTask.userId != null and queryTask.userId != \"\"'> " +
            "<if test='deptDTOs == null or deptDTOs.isEmpty()'>" + // 如果deptDTOs为空，则不包括OR前缀
            " (assigner_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR responsible_person_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR undertaker_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR leading_official_id LIKE CONCAT('%', #{queryTask.userId}, '%')) " +
            "</if>" +
            "<if test='deptDTOs != null and !deptDTOs.isEmpty()'>" + // 检查deptDTOs是否非空
            "OR (assigner_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR responsible_person_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR undertaker_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR leading_official_id LIKE CONCAT('%', #{queryTask.userId}, '%')) " +
            "</if>" +
            "</if>" +
            ") </if>" +
            "<if test='queryTask.leadingDepartmentId != null and queryTask.leadingDepartmentId != \"\"'> AND leading_department_id LIKE CONCAT('%', #{queryTask.leadingDepartmentId}, '%')</if>" +
            "<if test='queryTask.phoneUsed != null and queryTask.phoneUsed'> AND status != 9</if>" +
            "<if test='queryTask.untreated != null and queryTask.untreated'>" +
            " AND (" +
            "     (status = 12 AND process_instance_review_ids LIKE CONCAT('%', #{queryTask.userId}, '%'))" +
            "     <if test='queryTask.accept != null and queryTask.accept'>" +
            "         OR (status = 1 " +
            "<if test='deptDTOs != null and deptDTOs.size() > 0'>" +
            " AND ( " +
            "<foreach collection='deptDTOs' item='dept' separator=' OR '> " +
            "(leading_department_id LIKE CONCAT('%', #{dept.deptId}, '%')) " +
            "</foreach> " +
            " ) " +
            "</if>" +
            " ) " +
            "     </if>" +
            " )" +
            "</if>" +
            "<if test='queryTask.createdAtStart != null and queryTask.createdAtEnd != null'> AND created_at BETWEEN #{queryTask.createdAtStart} AND #{queryTask.createdAtEnd}</if>" +
            "</where>" +
            "GROUP BY task_period" +
            "</script>")
    List<Map<String, Object>> countTasksByTaskPeriod2(@Param("queryTask") TaskSearchDTO queryTask, @Param("deptDTOs") List<DeptDTO> deptDTOs);


    @Select("<script>" +
            "SELECT task_period, COUNT(*) AS count FROM task " +
            "WHERE status = 6 AND task_period IN (1, 2, 3, 4) " +
            "AND delete = false " +  // 添加delete=false条件
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
                                                              @Param("phoneUsed") Boolean phoneUsed,
                                                              @Param("createdAtStart") LocalDateTime createdAtStart,
                                                              @Param("createdAtEnd") LocalDateTime createdAtEnd);

    @Select("<script>" +
            "SELECT task_period, COUNT(*) AS count FROM task " +
            "<where>" + // 使用<where>标签代替WHERE 1=1
            "status = 6 AND task_period IN (1, 2, 3, 4) " +
            "AND delete = false " +  // 添加delete=false条件
            "<if test='queryTask.taskType != null'> AND task_type = #{queryTask.taskType}</if>" +
            "<if test='queryTask.source != null and queryTask.source != \"\"'> AND source LIKE CONCAT('%', #{queryTask.source}, '%')</if>" +
            "<if test='queryTask.leadingOfficialId != null and queryTask.leadingOfficialId != \"\"'> AND leading_official_id LIKE CONCAT('%', #{queryTask.leadingOfficialId}, '%')</if>" +
            "<if test='queryTask.assignerId != null and queryTask.assignerId != \"\"'> AND assigner_id LIKE CONCAT('%', #{queryTask.assignerId}, '%')</if>" +
            "<if test='queryTask.unAuth == null or !queryTask.unAuth'> AND (" +
            "<foreach collection='deptDTOs' item='dept' separator=' OR '> " +
            "(leading_department_id LIKE CONCAT('%', #{dept.deptId}, '%') OR co_organizer_id LIKE CONCAT('%', #{dept.deptId}, '%')) " +
            "</foreach> " +
            "<if test='queryTask.userId != null and queryTask.userId != \"\"'> " +
            "<if test='deptDTOs == null or deptDTOs.isEmpty()'>" + // 如果deptDTOs为空，则不包括OR前缀
            " (assigner_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR responsible_person_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR undertaker_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR leading_official_id LIKE CONCAT('%', #{queryTask.userId}, '%')) " +
            "</if>" +
            "<if test='deptDTOs != null and !deptDTOs.isEmpty()'>" + // 检查deptDTOs是否非空
            "OR (assigner_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR responsible_person_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR undertaker_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR leading_official_id LIKE CONCAT('%', #{queryTask.userId}, '%')) " +
            "</if>" +
            "</if>" +
            ") </if>" +
            "<if test='queryTask.leadingDepartmentId != null and queryTask.leadingDepartmentId != \"\"'> AND leading_department_id LIKE CONCAT('%', #{queryTask.leadingDepartmentId}, '%')</if>" +
            "<if test='queryTask.untreated != null and queryTask.untreated'>" +
            " AND (" +
            "     (status = 12 AND process_instance_review_ids LIKE CONCAT('%', #{queryTask.userId}, '%'))" +
            "     <if test='queryTask.accept != null and queryTask.accept'>" +
            "         OR (status = 1 " +
            "<if test='deptDTOs != null and deptDTOs.size() > 0'>" +
            " AND ( " +
            "<foreach collection='deptDTOs' item='dept' separator=' OR '> " +
            "(leading_department_id LIKE CONCAT('%', #{dept.deptId}, '%')) " +
            "</foreach> " +
            " ) " +
            "</if>" +
            " ) " +
            "     </if>" +
            " )" +
            "</if>" +
            "<if test='queryTask.createdAtStart != null and queryTask.createdAtEnd != null'> AND created_at BETWEEN #{queryTask.createdAtStart} AND #{queryTask.createdAtEnd}</if>" +
            "</where>" +
            "GROUP BY task_period" +
            "</script>")
    List<Map<String, Object>> countTasksByTaskPeriodAndStatus2(@Param("queryTask") TaskSearchDTO queryTask, @Param("deptDTOs") List<DeptDTO> deptDTOs);

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
            "AND delete = false " +  // 添加delete=false条件
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

    @Select("<script>" +
            "SELECT field_id, COUNT(*) AS count FROM task " +
            "<where>" + // 使用<where>标签代替WHERE 1=1
            "field_id IS NOT NULL " +
            "AND delete = false " +  // 添加delete=false条件
            "<if test='queryTask.taskType != null'> AND task_type = #{queryTask.taskType}</if>" +
            "<if test='queryTask.source != null and queryTask.source != \"\"'> AND source LIKE CONCAT('%', #{queryTask.source}, '%')</if>" +
            "<if test='queryTask.assignerId != null and queryTask.assignerId != \"\"'> AND assigner_id LIKE CONCAT('%', #{queryTask.assignerId}, '%')</if>" +
            "<if test='queryTask.overDue != null and queryTask.overDue'> AND overdue_days  IS NOT NULL AND overdue_days > 0 </if>" +
            "<if test='queryTask.leadingOfficialId != null and queryTask.leadingOfficialId != \"\"'> AND leading_official_id LIKE CONCAT('%', #{queryTask.leadingOfficialId}, '%')</if>" +
            "<if test='queryTask.unAuth == null or !queryTask.unAuth'> AND (" +
            "<foreach collection='deptDTOs' item='dept' separator=' OR '> " +
            "(leading_department_id LIKE CONCAT('%', #{dept.deptId}, '%') OR co_organizer_id LIKE CONCAT('%', #{dept.deptId}, '%')) " +
            "</foreach> " +
            "<if test='queryTask.userId != null and queryTask.userId != \"\"'> " +
            "<if test='deptDTOs == null or deptDTOs.isEmpty()'>" + // 如果deptDTOs为空，则不包括OR前缀
            " (assigner_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR responsible_person_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR undertaker_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR leading_official_id LIKE CONCAT('%', #{queryTask.userId}, '%')) " +
            "</if>" +
            "<if test='deptDTOs != null and !deptDTOs.isEmpty()'>" + // 检查deptDTOs是否非空
            "OR (assigner_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR responsible_person_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR undertaker_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR leading_official_id LIKE CONCAT('%', #{queryTask.userId}, '%')) " +
            "</if>" +
            "</if>" +
            ") </if>" +
            "<if test='queryTask.leadingDepartmentId != null and queryTask.leadingDepartmentId != \"\"'> AND leading_department_id LIKE CONCAT('%', #{queryTask.leadingDepartmentId}, '%')</if>" +
            "<if test='queryTask.createdAtStart != null and queryTask.createdAtEnd != null'> AND created_at BETWEEN #{queryTask.createdAtStart} AND #{queryTask.createdAtEnd}</if>" +
            "<if test='queryTask.untreated != null and queryTask.untreated'>" +
            " AND (" +
            "     (status = 12 AND process_instance_review_ids LIKE CONCAT('%', #{queryTask.userId}, '%'))" +
            "     <if test='queryTask.accept != null and queryTask.accept'>" +
            "         OR (status = 1 " +
            "<if test='deptDTOs != null and deptDTOs.size() > 0'>" +
            " AND ( " +
            "<foreach collection='deptDTOs' item='dept' separator=' OR '> " +
            "(leading_department_id LIKE CONCAT('%', #{dept.deptId}, '%')) " +
            "</foreach> " +
            " ) " +
            "</if>" +
            " ) " +
            "     </if>" +
            " )" +
            "</if>" +
            "<if test='queryTask.unfinished != null and queryTask.unfinished'> AND status NOT IN (6, 9)</if>" +
            "<if test='queryTask.status != null'>" +
            "<choose>" +
            "<when test='queryTask.status == 3'> AND status NOT IN (6, 9) AND overdue_days > 0</when>" +
            "<otherwise> AND status = #{queryTask.status}</otherwise>" +
            "</choose>" +
            "</if>" +
            "</where>" +
            "GROUP BY field_id" +
            "</script>")
    List<Map<String, Object>> countTasksByFieldId2(@Param("queryTask") TaskSearchDTO queryTask, @Param("deptDTOs") List<DeptDTO> deptDTOs);

    ////            "SELECT unnest(string_to_array(field_second_ids, ',')) AS field_id FROM task " +
    @Select("<script>" +
            "SELECT field_id, COUNT(*) AS count " +
            "FROM (" +
            "SELECT split_part(field_second_ids, ',', 1) AS field_id FROM task " +
            "<where>" + // 使用<where>标签代替WHERE 1=1
            "field_second_ids IS NOT NULL " +
            "AND delete = false " +  // 添加delete=false条件
            "<if test='queryTask.taskType != null'> AND task_type = #{queryTask.taskType}</if>" +
            "<if test='queryTask.thirdFieldIds != null and !queryTask.thirdFieldIds.isEmpty()'> " +
            " AND EXISTS ( " +
            "    SELECT 1 " +
            "    FROM unnest(string_to_array(field_third_ids, ',')) AS field_third_id " +
            "    WHERE field_third_id = ANY(string_to_array(#{queryTask.thirdFieldIds}, ',')::text[]) " +
            ") " +
            "</if>" +
            "<if test='queryTask.source != null and queryTask.source != \"\"'> AND source LIKE CONCAT('%', #{queryTask.source}, '%')</if>" +
            "<if test='queryTask.overDue != null'>\n" +
            "  <choose>" +
            "    <when test='queryTask.overDue'>" +
            "      AND overdue_days IS NOT NULL AND overdue_days > 0" +
            "    </when>" +
            "    <otherwise>\n" +
            "      AND (overdue_days IS NULL OR overdue_days = 0)" +
            "    </otherwise>" +
            "  </choose>" +
            "</if>" +
            "<if test='queryTask.assignerId != null and queryTask.assignerId != \"\"'> AND assigner_id LIKE CONCAT('%', #{queryTask.assignerId}, '%')</if>" +
            "<if test='queryTask.leadingOfficialId != null and queryTask.leadingOfficialId != \"\"'> AND leading_official_id LIKE CONCAT('%', #{queryTask.leadingOfficialId}, '%')</if>" +
            "<if test='queryTask.unAuth == null or !queryTask.unAuth'> AND (" +
            "<foreach collection='deptDTOs' item='dept' separator=' OR '> " +
            "(leading_department_id LIKE CONCAT('%', #{dept.deptId}, '%') OR co_organizer_id LIKE CONCAT('%', #{dept.deptId}, '%')) " +
            "</foreach> " +
            "<if test='queryTask.userId != null and queryTask.userId != \"\"'> " +
            "<if test='deptDTOs == null or deptDTOs.isEmpty()'>" + // 如果deptDTOs为空，则不包括OR前缀
            " (assigner_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR responsible_person_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR undertaker_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR leading_official_id LIKE CONCAT('%', #{queryTask.userId}, '%')) " +
            "</if>" +
            "<if test='deptDTOs != null and !deptDTOs.isEmpty()'>" + // 检查deptDTOs是否非空
            "OR (assigner_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR responsible_person_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR undertaker_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR leading_official_id LIKE CONCAT('%', #{queryTask.userId}, '%')) " +
            "</if>" +
            "</if>" +
            ") </if>" +
            "<if test='queryTask.leadingDepartmentId != null and queryTask.leadingDepartmentId != \"\"'> AND leading_department_id LIKE CONCAT('%', #{queryTask.leadingDepartmentId}, '%')</if>" +
            "<if test='queryTask.createdAtStart != null and queryTask.createdAtEnd != null'> AND created_at BETWEEN #{queryTask.createdAtStart} AND #{queryTask.createdAtEnd}</if>" +
            "<if test='queryTask.untreated != null and queryTask.untreated'>" +
            " AND (" +
            "     (status = 12 AND process_instance_review_ids LIKE CONCAT('%', #{queryTask.userId}, '%'))" +
            "     <if test='queryTask.accept != null and queryTask.accept'>" +
            "         OR (status = 1 " +
            "<if test='deptDTOs != null and deptDTOs.size() > 0'>" +
            " AND ( " +
            "<foreach collection='deptDTOs' item='dept' separator=' OR '> " +
            "(leading_department_id LIKE CONCAT('%', #{dept.deptId}, '%')) " +
            "</foreach> " +
            " ) " +
            "</if>" +
            " ) " +
            "     </if>" +
            " )" +
            "</if>" +
            "<if test='queryTask.unfinished != null and queryTask.unfinished'> AND status NOT IN (6, 9)</if>" +
            "<if test='queryTask.status != null'>" +
            "<choose>" +
            "<when test='queryTask.status == 3'> AND status NOT IN (6, 9) AND overdue_days > 0</when>" +
            "<otherwise> AND status = #{queryTask.status}</otherwise>" +
            "</choose>" +
            "</if>" +
            "</where>" +
            ") subquery " +
            "GROUP BY field_id" +
            "</script>")
    List<Map<String, Object>> countTasksByFieldId3(@Param("queryTask") TaskSearchDTO queryTask, @Param("deptDTOs") List<DeptDTO> deptDTOs);

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
            "AND delete = false " +  // 添加delete=false条件
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

    @Select("<script>" +
            "SELECT field_id, COUNT(*) AS count FROM task " +
            "<where>" + // 使用<where>标签代替WHERE 1=1
            "status = 6 " +
            "AND field_id IS NOT NULL " +
            "AND delete = false " +  // 添加delete=false条件
            "<if test='queryTask.taskType != null'> AND task_type = #{queryTask.taskType}</if>" +
            "<if test='queryTask.source != null and queryTask.source != \"\"'> AND source LIKE CONCAT('%', #{queryTask.source}, '%')</if>" +
            "<if test='queryTask.overDue != null and queryTask.overDue'> AND overdue_days  IS NOT NULL AND overdue_days > 0 </if>" +
            "<if test='queryTask.assignerId != null and queryTask.assignerId != \"\"'> AND assigner_id LIKE CONCAT('%', #{queryTask.assignerId}, '%')</if>" +
            "<if test='queryTask.leadingOfficialId != null and queryTask.leadingOfficialId != \"\"'> AND leading_official_id LIKE CONCAT('%', #{queryTask.leadingOfficialId}, '%')</if>" +
            "<if test='queryTask.unAuth == null or !queryTask.unAuth'> AND (" +
            "<foreach collection='deptDTOs' item='dept' separator=' OR '> " +
            "(leading_department_id LIKE CONCAT('%', #{dept.deptId}, '%') OR co_organizer_id LIKE CONCAT('%', #{dept.deptId}, '%')) " +
            "</foreach> " +
            "<if test='queryTask.userId != null and queryTask.userId != \"\"'> " +
            "<if test='deptDTOs == null or deptDTOs.isEmpty()'>" + // 如果deptDTOs为空，则不包括OR前缀
            " (assigner_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR responsible_person_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR undertaker_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR leading_official_id LIKE CONCAT('%', #{queryTask.userId}, '%')) " +
            "</if>" +
            "<if test='deptDTOs != null and !deptDTOs.isEmpty()'>" + // 检查deptDTOs是否非空
            "OR (assigner_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR responsible_person_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR undertaker_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR leading_official_id LIKE CONCAT('%', #{queryTask.userId}, '%')) " +
            "</if>" +
            "</if>" +
            ") </if>" +
            "<if test='queryTask.leadingDepartmentId != null and queryTask.leadingDepartmentId != \"\"'> AND leading_department_id LIKE CONCAT('%', #{queryTask.leadingDepartmentId}, '%')</if>" +
            "<if test='queryTask.untreated != null and queryTask.untreated'>" +
            " AND (" +
            "     (status = 12 AND process_instance_review_ids LIKE CONCAT('%', #{queryTask.userId}, '%'))" +
            "     <if test='queryTask.accept != null and queryTask.accept'>" +
            "         OR (status = 1 " +
            "<if test='deptDTOs != null and deptDTOs.size() > 0'>" +
            " AND ( " +
            "<foreach collection='deptDTOs' item='dept' separator=' OR '> " +
            "(leading_department_id LIKE CONCAT('%', #{dept.deptId}, '%')) " +
            "</foreach> " +
            " ) " +
            "</if>" +
            " ) " +
            "     </if>" +
            " )" +
            "</if>" +
            "<if test='queryTask.createdAtStart != null and queryTask.createdAtEnd != null'> AND created_at BETWEEN #{queryTask.createdAtStart} AND #{queryTask.createdAtEnd}</if>" +
            "</where>" +
            "GROUP BY field_id" +
            "</script>")
    List<Map<String, Object>> countTasksByFieldIdAndStatus2(@Param("queryTask") TaskSearchDTO queryTask, @Param("deptDTOs") List<DeptDTO> deptDTOs);

    @Select("<script>" +
            "SELECT field_id, COUNT(*) AS count " +
            "FROM (" +
            "SELECT split_part(field_second_ids, ',', 1) AS field_id FROM task " +
            "<where>" + // 使用<where>标签代替WHERE 1=1
            "field_second_ids IS NOT NULL " +
            "AND status = 6 " +
            "AND delete = false " +  // 添加delete=false条件
            "<if test='queryTask.taskType != null'> AND task_type = #{queryTask.taskType}</if>" +
            "<if test='queryTask.thirdFieldIds != null and !queryTask.thirdFieldIds.isEmpty()'> " +
            " AND EXISTS ( " +
            "    SELECT 1 " +
            "    FROM unnest(string_to_array(field_third_ids, ',')) AS field_third_id " +
            "    WHERE field_third_id = ANY(string_to_array(#{queryTask.thirdFieldIds}, ',')::text[]) " +
            ") " +
            "</if>" +
            "<if test='queryTask.source != null and queryTask.source != \"\"'> AND source LIKE CONCAT('%', #{queryTask.source}, '%')</if>" +
            "<if test='queryTask.assignerId != null and queryTask.assignerId != \"\"'> AND assigner_id LIKE CONCAT('%', #{queryTask.assignerId}, '%')</if>" +
            "<if test='queryTask.overDue != null'>\n" +
            "  <choose>" +
            "    <when test='queryTask.overDue'>" +
            "      AND overdue_days IS NOT NULL AND overdue_days > 0" +
            "    </when>" +
            "    <otherwise>\n" +
            "      AND (overdue_days IS NULL OR overdue_days = 0)" +
            "    </otherwise>" +
            "  </choose>" +
            "</if>" +
            "<if test='queryTask.leadingOfficialId != null and queryTask.leadingOfficialId != \"\"'> AND leading_official_id LIKE CONCAT('%', #{queryTask.leadingOfficialId}, '%')</if>" +
            "<if test='queryTask.unAuth == null or !queryTask.unAuth'> AND (" +
            "<foreach collection='deptDTOs' item='dept' separator=' OR '> " +
            "(leading_department_id LIKE CONCAT('%', #{dept.deptId}, '%') OR co_organizer_id LIKE CONCAT('%', #{dept.deptId}, '%')) " +
            "</foreach> " +
            "<if test='queryTask.userId != null and queryTask.userId != \"\"'> " +
            "<if test='deptDTOs == null or deptDTOs.isEmpty()'>" + // 如果deptDTOs为空，则不包括OR前缀
            " (assigner_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR responsible_person_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR undertaker_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR leading_official_id LIKE CONCAT('%', #{queryTask.userId}, '%')) " +
            "</if>" +
            "<if test='deptDTOs != null and !deptDTOs.isEmpty()'>" + // 检查deptDTOs是否非空
            "OR (assigner_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR responsible_person_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR undertaker_id LIKE CONCAT('%', #{queryTask.userId}, '%') OR leading_official_id LIKE CONCAT('%', #{queryTask.userId}, '%')) " +
            "</if>" +
            "</if>" +
            ") </if>" +
            "<if test='queryTask.leadingDepartmentId != null and queryTask.leadingDepartmentId != \"\"'> AND leading_department_id LIKE CONCAT('%', #{queryTask.leadingDepartmentId}, '%')</if>" +
            "<if test='queryTask.createdAtStart != null and queryTask.createdAtEnd != null'> AND created_at BETWEEN #{queryTask.createdAtStart} AND #{queryTask.createdAtEnd}</if>" +
            "<if test='queryTask.untreated != null and queryTask.untreated'>" +
            " AND (" +
            "     (status = 12 AND process_instance_review_ids LIKE CONCAT('%', #{queryTask.userId}, '%'))" +
            "     <if test='queryTask.accept != null and queryTask.accept'>" +
            "         OR (status = 1 " +
            "<if test='deptDTOs != null and deptDTOs.size() > 0'>" +
            " AND ( " +
            "<foreach collection='deptDTOs' item='dept' separator=' OR '> " +
            "(leading_department_id LIKE CONCAT('%', #{dept.deptId}, '%')) " +
            "</foreach> " +
            " ) " +
            "</if>" +
            " ) " +
            "     </if>" +
            " )" +
            "</if>" +
            "<if test='queryTask.unfinished != null and queryTask.unfinished'> AND status NOT IN (6, 9)</if>" +
            "<if test='queryTask.status != null'>" +
            "<choose>" +
            "<when test='queryTask.status == 3'> AND status NOT IN (6, 9) AND overdue_days > 0</when>" +
            "<otherwise> AND status = #{queryTask.status}</otherwise>" +
            "</choose>" +
            "</if>" +
            "</where>" +
            ") subquery " +
            "GROUP BY field_id" +
            "</script>")
    List<Map<String, Object>> countTasksByFieldIdAndStatus3(@Param("queryTask") TaskSearchDTO queryTask, @Param("deptDTOs") List<DeptDTO> deptDTOs);

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
            "AND delete = false " +  // 添加delete=false条件
            "AND deadline IS NOT NULL")
    void updateOverdueDays();

    /**
     * 自定义 SQL 更新 count_down_days 列
     */
    @Update("UPDATE public.task " +
            "SET count_down_days = GREATEST((deadline::date - CURRENT_DATE), 0) " +
            "WHERE status NOT IN (6, 9) " +
            "AND count_down IS NOT NULL " +
            "AND deadline IS NOT NULL " +
            "AND CURRENT_DATE BETWEEN count_down::date AND deadline::date")
    int updateCountDownDays();

    /**
     * 统计 count_down_days > 0 的记录个数
     */
    @Select("SELECT COUNT(*) " +
            "FROM public.task " +
            "WHERE status NOT IN (6, 9) " +
            "AND count_down_days > 0")
    long countCountDownDays();

    /**
     * "SET is_filled = CASE",
     * "    WHEN pr.created_at IS NULL THEN false",
     * "    WHEN DATE_TRUNC('day', NOW())::date - DATE_TRUNC('day', pr.created_at)::date < t_main.fill_cycle THEN true",
     * "    ELSE false",
     * "END",
     *
     * @return
     */
    @Update({
            "UPDATE public.task t_main",
            "SET is_filled = CASE",
            "    WHEN DATE_TRUNC('day', NOW())::date - DATE_TRUNC('day', COALESCE(pr.created_at, t_main.created_at))::date < t_main.fill_cycle THEN true",
            "    ELSE false",
            "END",
            "FROM (",
            "    SELECT DISTINCT ON (task_id)",
            "        id,",
            "        task_id,",
            "        created_at,",
            "        status,",
            "        delete",
            "    FROM public.progress_report",
            "    WHERE delete = false AND status = 3",
            "    ORDER BY task_id, created_at DESC",
            ") pr",
            "RIGHT JOIN public.task t",
            "    ON t.id = pr.task_id",
            "WHERE t.delete = false",
            "  AND t.id = t_main.id"
    })
    int updateIsFilled();
}