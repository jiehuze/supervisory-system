package com.schedule.supervisory.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schedule.supervisory.entity.BzIssueTargetRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BzIssueTargetRecordMapper extends BaseMapper<BzIssueTargetRecord> {
    List<BzIssueTargetRecord> selectByTargetId(@Param("targetId") Integer targetId);
}