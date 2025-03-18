package com.schedule.supervisory.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.supervisory.dto.BzFromTargetNameCount;
import com.schedule.supervisory.dto.BzSearchDTO;
import com.schedule.supervisory.dto.DeptDTO;
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

    @Select("<script>" +
            "SELECT * FROM bz_form bf " +
            "LEFT JOIN bz_type bt ON bf.type_id = bt.type_id AND bt.type = '1' " +
            "<where>" +
            "<if test='queryBzForm.typeId != null'> AND bf.type_id = #{queryBzForm.typeId}</if>" +
            "<if test='queryBzForm.predictedGear != null'> AND bf.predicted_gear = #{queryBzForm.predictedGear}</if>" +
            "<if test='queryBzForm.actualGear != null'> AND bf.actual_gear = #{queryBzForm.actualGear}</if>" +
            "<if test='queryBzForm.createdAtStart != null and queryBzForm.createdAtEnd != null'> AND bf.created_at BETWEEN #{queryBzForm.createdAtStart} AND #{queryBzForm.createdAtEnd}</if>" +
            "<if test='queryBzForm.dateType != null'> AND bf.date_type = #{queryBzForm.dateType}</if>" +
            "<if test='queryBzForm.year != null'> AND bf.year = #{queryBzForm.year}</if>" +
            "<if test='queryBzForm.quarter != null'> AND bf.quarter = #{queryBzForm.quarter}</if>" +
            "<if test='queryBzForm.unAuth == null or !queryBzForm.unAuth'> AND (" +
            "<foreach collection='deptDTOs' item='dept' separator=' OR '> " +
            "(leading_department_id LIKE CONCAT('%', #{dept.deptId}, '%') OR responsible_dept_id LIKE CONCAT('%', #{dept.deptId}, '%')) " +
            "</foreach> " +
            "<if test='queryBzForm.userId != null and queryBzForm.userId != \"\"'> " +
            "<if test='deptDTOs == null or deptDTOs.isEmpty()'>" + // 如果deptDTOs为空，则不包括OR前缀
            " (assigner_id LIKE CONCAT('%', #{queryBzForm.userId}, '%')) " +
            "</if>" +
            "<if test='deptDTOs != null and !deptDTOs.isEmpty()'>" + // 检查deptDTOs是否非空
            "OR (assigner_id LIKE CONCAT('%', #{queryBzForm.userId}, '%')) " +
            "</if>" +
            "</if>" +
            ") </if>" +
            "</where>" +
            "ORDER BY bt.order_num ASC" +
            "</script>")
    Page<BzForm> getBzFormByConditions(@Param("page") Page<BzForm> page,
                                       @Param("queryBzForm") BzSearchDTO queryBzForm,
                                       @Param("deptDTOs") List<DeptDTO> deptDTOs);

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