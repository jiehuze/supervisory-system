package com.schedule.supervisory.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.schedule.supervisory.entity.BzInstruction;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 标记为MyBatis的Mapper接口
public interface BzInstructionMapper extends BaseMapper<BzInstruction> {
}
