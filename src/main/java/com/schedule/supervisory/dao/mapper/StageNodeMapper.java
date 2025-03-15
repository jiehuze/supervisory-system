package com.schedule.supervisory.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schedule.supervisory.entity.StageNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface StageNodeMapper extends BaseMapper<StageNode> {
    List<StageNode> selectByTaskIdOrderByCreatedAtDesc(@Param("taskId") Integer taskId);

    @Update("UPDATE stage_node " +
            "SET overdue_days = GREATEST((CURRENT_DATE - deadline), 0) " +
            "WHERE status NOT IN (2, 4) " +
            "AND CURRENT_DATE > deadline " + // 修改这里使用CURRENT_DATE代替updated_at
            "AND deadline IS NOT NULL")
    void updateOverdueDays();
}