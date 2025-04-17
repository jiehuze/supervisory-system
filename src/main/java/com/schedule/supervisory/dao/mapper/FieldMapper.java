package com.schedule.supervisory.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schedule.supervisory.entity.Field;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FieldMapper extends BaseMapper<Field> {
    @Insert("INSERT INTO public.field (name, description, delete, parent_id) " +
            "VALUES (#{entity.name}, #{entity.description}, #{entity.delete}, #{entity.parentId})")
    int insert(@Param("entity") Field entity);
}
