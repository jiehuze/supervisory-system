package com.schedule.supervisory.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schedule.supervisory.dto.BzFromTargetNameCount;
import com.schedule.supervisory.dto.EffectiveGearCount;
import com.schedule.supervisory.entity.BzIssue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface BzIssueMapper extends BaseMapper<BzIssue> {
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
            "GROUP BY name " +
            "ORDER BY name")
    List<BzFromTargetNameCount> selectByTimeAndGear(@Param("startTime") LocalDateTime startTime,
                                                    @Param("endTime") LocalDateTime endTime,
                                                    @Param("gear") Integer gear);
}