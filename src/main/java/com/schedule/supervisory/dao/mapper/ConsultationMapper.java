package com.schedule.supervisory.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schedule.supervisory.entity.Consultation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConsultationMapper extends BaseMapper<Consultation> {
    // MyBatis-Plus 提供了常用的 CRUD 方法，无需额外实现
}