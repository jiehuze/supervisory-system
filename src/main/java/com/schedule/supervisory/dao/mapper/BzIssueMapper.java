package com.schedule.supervisory.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schedule.supervisory.entity.BzIssue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface BzIssueMapper extends BaseMapper<BzIssue> {
    @Select("SELECT type_id, COALESCE(actual_gear, predicted_gear) AS effective_gear, COUNT(*) AS count_effective_gear " +
            "FROM public.bz_issue " +
            "GROUP BY type_id, COALESCE(actual_gear, predicted_gear) " +
            "ORDER BY type_id, effective_gear")
    List<Map<String, Object>> countEffectiveGear();
}