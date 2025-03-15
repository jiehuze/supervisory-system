package com.schedule.supervisory.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.supervisory.dto.BzFromTargetNameCount;
import com.schedule.supervisory.dto.EffectiveGearCount;
import com.schedule.supervisory.entity.BzForm;
import com.schedule.supervisory.entity.BzFormTarget;
import com.schedule.supervisory.entity.Task;
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
            "RIGHT JOIN public.bz_form_target bft ON bf.id = bft.bz_form_id\n" +
            "WHERE COALESCE(bft.actual_gear, bft.predicted_gear) BETWEEN 1 AND 4\n" +
            "AND bft.delete = FALSE " +
            "GROUP BY bf.type_id, COALESCE(bft.actual_gear, bft.predicted_gear)\n" +
            "ORDER BY bf.type_id, effective_gear;")
    List<Map<String, Object>> countEffectiveGear();

    @Select("SELECT COALESCE(actual_gear, predicted_gear) AS effective_gear, COUNT(*) AS count_effective_gear " +
            "FROM public.bz_form_target " +
            "WHERE COALESCE(actual_gear, predicted_gear) BETWEEN 1 AND 4 " +
//            "AND created_at BETWEEN #{startTime} AND #{endTime}" +
            "GROUP BY COALESCE(actual_gear, predicted_gear) " +
            "ORDER BY effective_gear")
    List<EffectiveGearCount> countGearCollect();

    @Select("SELECT COALESCE(actual_gear, predicted_gear) AS effective_gear, COUNT(*) AS count_effective_gear " +
            "FROM public.bz_form_target " +
            "WHERE COALESCE(actual_gear, predicted_gear) BETWEEN 1 AND 4 " +
            "AND delete = FALSE " +
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

    @Select("<script>" +
            "SELECT bft.name, COUNT(*) AS count " +
            "FROM public.bz_form_target bft " +
            "LEFT JOIN public.bz_form bf ON bf.id = bft.bz_form_id " +
            "<where>" + // 使用<where>标签代替WHERE 1=1
            "<if test='typeId != null and typeId != 0'> bf.type_id = #{typeId} </if>" +
            "AND CASE WHEN bft.actual_gear IS NOT NULL THEN bft.actual_gear ELSE bft.predicted_gear END = #{gear} " +
            "</where>" +
            "GROUP BY bft.name ORDER BY bft.name" +
            "</script>")
    List<BzFromTargetNameCount> selectByTimeAndGear(@Param("startTime") LocalDateTime startTime,
                                                    @Param("endTime") LocalDateTime endTime,
                                                    @Param("gear") Integer gear,
                                                    @Param("typeId") Integer typeId);

    @Select("<script>" +
            "SELECT * " +
            "FROM public.bz_form_target bft " +
            "LEFT JOIN public.bz_form bf ON bf.id = bft.bz_form_id " +
            "<where>" + // 使用<where>标签代替WHERE 1=1
            "<if test='typeId != null and typeId != 0'> bf.type_id = #{typeId} </if>" +
            "AND CASE WHEN bft.actual_gear IS NOT NULL THEN bft.actual_gear ELSE bft.predicted_gear END = #{gear} " +
            "AND bft.delete = FALSE " +
            "</where>" +
            "</script>")
    Page<BzFormTarget> selectByTypeAndGear(Page<BzFormTarget> page,
                                           @Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime,
                                           @Param("gear") Integer gear,
                                           @Param("typeId") Integer typeId);
}