package com.schedule.supervisory.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schedule.supervisory.entity.BzFormTargetRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BzFormTargetRecordMapper extends BaseMapper<BzFormTargetRecord> {
    List<BzFormTargetRecord> selectByTargetId(@Param("targetId") Integer targetId);
}