package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.InstructionMapper;
import com.schedule.supervisory.dto.BzSearchDTO;
import com.schedule.supervisory.entity.Instruction;
import com.schedule.supervisory.service.IInstructionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstructionServiceImpl extends ServiceImpl<InstructionMapper, Instruction> implements IInstructionService {
    @Override
    public List<Instruction> getInstructionsByTaskId(BzSearchDTO bzSearchDTO) {
        LambdaQueryWrapper<Instruction> queryWrapper = new LambdaQueryWrapper<>();
        if (bzSearchDTO.getTaskId() != null) {
            queryWrapper.eq(Instruction::getTaskId, bzSearchDTO.getTaskId()); // 根据taskId进行查询
        }
        if (bzSearchDTO.getBzFormId() != null) {
            queryWrapper.eq(Instruction::getBzFormId, bzSearchDTO.getBzFormId());
        }
        if (bzSearchDTO.getBzIssueId() != null) {
            queryWrapper.eq(Instruction::getBzIssueId, bzSearchDTO.getBzIssueId());
        }
        if (bzSearchDTO.getBzFormTargetId() != null) {
            queryWrapper.eq(Instruction::getBzFormTargetId, bzSearchDTO.getBzFormTargetId());
        }
        if (bzSearchDTO.getBzIssueTargetId() != null) {
            queryWrapper.eq(Instruction::getBzIssueTargetId, bzSearchDTO.getBzIssueTargetId());
        }
        queryWrapper.orderByDesc(Instruction::getId);
        return this.list(queryWrapper);
    }
}