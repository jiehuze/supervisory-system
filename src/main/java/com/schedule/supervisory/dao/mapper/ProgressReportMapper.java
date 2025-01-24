package com.schedule.supervisory.dao.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schedule.supervisory.entity.ProgressReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProgressReportMapper extends BaseMapper<ProgressReport> {
    List<ProgressReport> selectByTaskIdOrderByCreatedAtDesc(@Param("taskId") Long taskId);
}