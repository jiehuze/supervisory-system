package com.schedule.supervisory.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.supervisory.dto.BzFromTargetNameCount;
import com.schedule.supervisory.dto.BzSearchDTO;
import com.schedule.supervisory.dto.DeptDTO;
import com.schedule.supervisory.dto.EffectiveGearCount;
import com.schedule.supervisory.entity.BzForm;
import com.schedule.supervisory.entity.BzIssue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface BzIssueMapper extends BaseMapper<BzIssue> {
    @Select("<script>" +
            "SELECT * FROM bz_issue bf " +
            "LEFT JOIN bz_type bt ON bf.type_id = bt.type_id AND bt.type = '2' " +
            "<where>" +
            "<if test='queryBzIssue.bzIssueId != null'> AND bf.id = #{queryBzIssue.bzIssueId}</if>" +
            "<if test='queryBzIssue.typeId != null'> AND bf.type_id = #{queryBzIssue.typeId}</if>" +
            "<if test='queryBzIssue.predictedGear != null'> AND bf.predicted_gear = #{queryBzIssue.predictedGear}</if>" +
            "<if test='queryBzIssue.actualGear != null'> AND bf.actual_gear = #{queryBzIssue.actualGear}</if>" +
            "<if test='queryBzIssue.createdAtStart != null and queryBzIssue.createdAtEnd != null'> AND bf.created_at BETWEEN #{queryBzIssue.createdAtStart} AND #{queryBzIssue.createdAtEnd}</if>" +
            "<if test='queryBzIssue.dateType != null'> AND bf.date_type = #{queryBzIssue.dateType}</if>" +
            "<if test='queryBzIssue.year != null'> AND bf.year = #{queryBzIssue.year}</if>" +
            "<if test='queryBzIssue.quarter != null'> AND bf.quarter = #{queryBzIssue.quarter}</if>" +
            "<if test='queryBzIssue.unAuth == null or !queryBzIssue.unAuth'> AND (" +
            "<foreach collection='deptDTOs' item='dept' separator=' OR '> " +
            "(leading_department_id LIKE CONCAT('%', #{dept.deptId}, '%') OR responsible_dept_id LIKE CONCAT('%', #{dept.deptId}, '%')) " +
            "</foreach> " +
            "<if test='queryBzIssue.userId != null and queryBzIssue.userId != \"\"'> " +
            "<if test='deptDTOs == null or deptDTOs.isEmpty()'>" + // 如果deptDTOs为空，则不包括OR前缀
            " (assigner_id LIKE CONCAT('%', #{queryBzIssue.userId}, '%')) " +
            "</if>" +
            "<if test='deptDTOs != null and !deptDTOs.isEmpty()'>" + // 检查deptDTOs是否非空
            "OR (assigner_id LIKE CONCAT('%', #{queryBzIssue.userId}, '%')) " +
            "</if>" +
            "</if>" +
            ") </if>" +
            "</where>" +
            "ORDER BY bt.order_num ASC" +
            "</script>")
    Page<BzIssue> getBzIssueByConditions(@Param("page") Page<BzIssue> page,
                                         @Param("queryBzIssue") BzSearchDTO queryBzIssue,
                                         @Param("deptDTOs") List<DeptDTO> deptDTOs);

    @Select("SELECT type_id, COALESCE(actual_gear, predicted_gear) AS effective_gear, COUNT(*) AS count_effective_gear " +
            "FROM public.bz_issue " +
            "GROUP BY type_id, COALESCE(actual_gear, predicted_gear) " +
            "ORDER BY type_id, effective_gear")
    List<Map<String, Object>> countEffectiveGear();

    @Select("SELECT COALESCE(actual_gear, predicted_gear) AS effective_gear, COUNT(*) AS count_effective_gear " +
            "FROM public.bz_issue " +
            "WHERE type_id BETWEEN 1 AND 8 " +
            "AND COALESCE(actual_gear, predicted_gear) BETWEEN 1 AND 5 " +
            "GROUP BY COALESCE(actual_gear, predicted_gear) " +
            "ORDER BY effective_gear")
    List<EffectiveGearCount> countGearCollect();

    @Select("SELECT COALESCE(actual_gear, predicted_gear) AS effective_gear, COUNT(*) AS count_effective_gear " +
            "FROM public.bz_issue_target " +
            "WHERE COALESCE(actual_gear, predicted_gear) BETWEEN 1 AND 4 " +
            "AND created_at BETWEEN #{startTime} AND #{endTime}" +
            "AND delete = FALSE " +
            "GROUP BY COALESCE(actual_gear, predicted_gear) " +
            "ORDER BY effective_gear")
    List<EffectiveGearCount> countGearCollectTargetByDate(@Param("startTime") LocalDateTime startTime,
                                                          @Param("endTime") LocalDateTime endTime);

    @Select("SELECT COALESCE(actual_gear, predicted_gear) AS effective_gear, COUNT(*) AS count_effective_gear " +
            "FROM public.bz_issue " +
            "WHERE COALESCE(actual_gear, predicted_gear) BETWEEN 1 AND 5 " +
            "AND created_at BETWEEN #{startTime} AND #{endTime}" +
            "GROUP BY COALESCE(actual_gear, predicted_gear) " +
            "ORDER BY effective_gear")
    List<EffectiveGearCount> countGearCollectByDate(@Param("startTime") LocalDateTime startTime,
                                                    @Param("endTime") LocalDateTime endTime);

    @Select("SELECT name, COUNT(*) AS count " +
            "FROM public.bz_issue_target " +
            "WHERE updated_at BETWEEN #{startTime} AND #{endTime} " +
            "AND CASE WHEN actual_gear IS NOT NULL THEN actual_gear ELSE predicted_gear END = #{gear} " +
            "AND delete = FALSE " +
            "GROUP BY name " +
            "ORDER BY name")
    List<BzFromTargetNameCount> selectByTimeAndGear(@Param("startTime") LocalDateTime startTime,
                                                    @Param("endTime") LocalDateTime endTime,
                                                    @Param("gear") Integer gear);
}