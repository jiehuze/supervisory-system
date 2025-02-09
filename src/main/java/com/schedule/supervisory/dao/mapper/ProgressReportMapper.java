package com.schedule.supervisory.dao.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schedule.supervisory.dto.TaskWithProgressReportDTO;
import com.schedule.supervisory.entity.ProgressReport;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProgressReportMapper extends BaseMapper<ProgressReport> {
    List<ProgressReport> selectByTaskIdOrderByCreatedAtDesc(@Param("taskId") Long taskId);

    @Select("SELECT t.id AS task_id, pr.created_at AS last_progress_created_at, " +
            "(CURRENT_DATE - pr.created_at::date) AS days_diff, t.fill_cycle, t.status, " +
            "t.handler_id, t.responsible_person_id, t.source, t.source_date " +
            "FROM public.task t " +
            "JOIN (SELECT task_id, MAX(created_at) AS created_at " +
            "      FROM public.progress_report " +
            "      GROUP BY task_id) pr ON t.id = pr.task_id " +
            "WHERE (CURRENT_DATE - pr.created_at::date) > t.fill_cycle " +
            "AND t.status NOT IN (6, 9)")
    @Results({
            @Result(property = "taskId", column = "task_id"),
            @Result(property = "lastProgressCreatedAt", column = "last_progress_created_at"),
            @Result(property = "daysDiff", column = "days_diff"),
            @Result(property = "fillCycle", column = "fill_cycle"),
            @Result(property = "status", column = "status"),
            @Result(property = "source", column = "source"),
            @Result(property = "sourceDate", column = "source_date"),
            @Result(property = "handlerId", column = "handler_id"),
            @Result(property = "responsiblePersonId", column = "responsible_person_id")
    })
    List<TaskWithProgressReportDTO> findTasksWithConditions();
}