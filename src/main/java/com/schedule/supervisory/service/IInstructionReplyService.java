package com.schedule.supervisory.service;

import com.schedule.supervisory.entity.InstructionReply;

import java.util.List;

public interface IInstructionReplyService {

    // 插入一条回复记录
    int addReply(InstructionReply reply);

    // 根据 instruction_id 查询回复列表
    List<InstructionReply> getRepliesByInstructionId(Integer instructionId);
}