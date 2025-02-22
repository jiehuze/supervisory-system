package com.schedule.supervisory.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schedule.supervisory.dto.BzFromTargetNameCount;
import com.schedule.supervisory.dto.EffectiveGearCount;
import com.schedule.supervisory.entity.BzForm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface BzFormMapper extends BaseMapper<BzForm> {
    //    @Select("SELECT type_id, COALESCE(actual_gear, predicted_gear) AS effective_gear, COUNT(*) AS count_effective_gear " +
//            "FROM public.bz_form " +
//            "GROUP BY type_id, COALESCE(actual_gear, predicted_gear) " +
//            "ORDER BY type_id, effective_gear")
    @Select("SELECT bf.type_id, \n" +
            "       COALESCE(bft.actual_gear, bft.predicted_gear) AS effective_gear, \n" +
            "       COUNT(*) AS count_effective_gear \n" +
            "FROM public.bz_form bf\n" +
            "LEFT JOIN public.bz_form_target bft ON bf.id = bft.bz_form_id\n" +
            "WHERE COALESCE(bft.actual_gear, bft.predicted_gear) BETWEEN 1 AND 4\n" +
            "GROUP BY bf.type_id, COALESCE(bft.actual_gear, bft.predicted_gear)\n" +
            "ORDER BY bf.type_id, effective_gear;")
    List<Map<String, Object>> countEffectiveGear();

    @Select("SELECT COALESCE(actual_gear, predicted_gear) AS effective_gear, COUNT(*) AS count_effective_gear " +
            "FROM public.bz_form_target " +
            "WHERE COALESCE(actual_gear, predicted_gear) BETWEEN 1 AND 4 " +
            "GROUP BY COALESCE(actual_gear, predicted_gear) " +
            "ORDER BY effective_gear")
    List<EffectiveGearCount> countGearCollect();

    @Select("SELECT COALESCE(actual_gear, predicted_gear) AS effective_gear, COUNT(*) AS count_effective_gear " +
            "FROM public.bz_form_target " +
            "WHERE COALESCE(actual_gear, predicted_gear) BETWEEN 1 AND 4 " +
            "AND created_at BETWEEN #{startTime} AND #{endTime}" +
            "GROUP BY COALESCE(actual_gear, predicted_gear) " +
            "ORDER BY effective_gear")
    List<EffectiveGearCount> countGearCollectTargetByDate(@Param("startTime") LocalDateTime startTime,
                                                          @Param("endTime") LocalDateTime endTime);

    @Select("SELECT COALESCE(actual_gear, predicted_gear) AS effective_gear, COUNT(*) AS count_effective_gear " +
            "FROM public.bz_form " +
            "WHERE COALESCE(actual_gear, predicted_gear) BETWEEN 1 AND 5 " +
            "AND created_at BETWEEN #{startTime} AND #{endTime}" +
            "GROUP BY COALESCE(actual_gear, predicted_gear) " +
            "ORDER BY effective_gear")
    List<EffectiveGearCount> countGearCollectByDate(@Param("startTime") LocalDateTime startTime,
                                                    @Param("endTime") LocalDateTime endTime);

    @Select("SELECT name, COUNT(*) AS count " +
            "FROM public.bz_form_target " +
            "WHERE updated_at BETWEEN #{startTime} AND #{endTime} " +
            "AND CASE WHEN actual_gear IS NOT NULL THEN actual_gear ELSE predicted_gear END = #{gear} " +
            "GROUP BY name " +
            "ORDER BY name")
    List<BzFromTargetNameCount> selectByTimeAndGear(@Param("startTime") LocalDateTime startTime,
                                                    @Param("endTime") LocalDateTime endTime,
                                                    @Param("gear") Integer gear);
}