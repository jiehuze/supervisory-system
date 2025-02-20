package com.schedule.supervisory.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schedule.supervisory.entity.BzType;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BzTypeMapper extends BaseMapper<BzType> {
    // 由于 BaseMapper 已经提供了基础的 CRUD 操作方法，这里不需要额外定义
}