package com.schedule.supervisory.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schedule.supervisory.entity.StageNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface StageNodeMapper extends BaseMapper<StageNode> {
    List<StageNode> selectByTaskIdOrderByCreatedAtDesc(@Param("taskId") Integer taskId);
}