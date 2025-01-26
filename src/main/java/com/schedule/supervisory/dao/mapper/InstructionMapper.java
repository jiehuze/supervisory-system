package com.schedule.supervisory.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schedule.supervisory.entity.Instruction;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 标记为MyBatis的Mapper接口
public interface InstructionMapper extends BaseMapper<Instruction> {
    // 可以根据需要添加自定义查询方法
}