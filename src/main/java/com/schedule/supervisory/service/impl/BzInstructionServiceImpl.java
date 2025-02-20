package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.BzInstructionMapper;
import com.schedule.supervisory.entity.BzInstruction;
import com.schedule.supervisory.service.IBzInstructionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BzInstructionServiceImpl extends ServiceImpl<BzInstructionMapper, BzInstruction> implements IBzInstructionService {
    @Override
    public List<BzInstruction> getInstructionsByContains(BzInstruction bzInstruction) {
        LambdaQueryWrapper<BzInstruction> queryWrapper = new LambdaQueryWrapper<>();
        if (bzInstruction.getBzIssueId() != null) {
            queryWrapper.eq(BzInstruction::getBzIssueId, bzInstruction.getBzIssueId()); // 根据taskId进行查询
        }
        if (bzInstruction.getBzFormId() != null) {
            queryWrapper.eq(BzInstruction::getBzFormId, bzInstruction.getBzFormId()); // 根据taskId进行查询
        }
        if (bzInstruction.getBzTargetId() != null) {
            queryWrapper.eq(BzInstruction::getBzTargetId, bzInstruction.getBzTargetId()); // 根据taskId进行查询
        }
        return this.list(queryWrapper);
    }
}
