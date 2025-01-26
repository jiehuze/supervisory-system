package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.InstructionMapper;
import com.schedule.supervisory.entity.Instruction;
import com.schedule.supervisory.service.IInstructionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstructionServiceImpl extends ServiceImpl<InstructionMapper, Instruction> implements IInstructionService {
    @Override
    public List<Instruction> getInstructionsByTaskId(Integer taskId) {
        LambdaQueryWrapper<Instruction> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Instruction::getTaskId, taskId); // 根据taskId进行查询
        return this.list(queryWrapper);
    }
}