package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.InstructionReplyMapper;
import com.schedule.supervisory.entity.InstructionReply;
import com.schedule.supervisory.service.IInstructionReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstructionReplyServiceImpl extends ServiceImpl<InstructionReplyMapper, InstructionReply> implements IInstructionReplyService {

    @Autowired
    private InstructionReplyMapper instructionReplyMapper;

    @Override
    public int addReply(InstructionReply reply) {
//        return this.save(reply); // 使用 MyBatis-Plus 的 save 方法插入数据
        return instructionReplyMapper.insert(reply);
    }

    @Override
    public List<InstructionReply> getRepliesByInstructionId(Integer instructionId) {
//        return this.baseMapper.selectByInstructionId(instructionId);
        return instructionReplyMapper.selectByInstructionId(instructionId);
    }
}