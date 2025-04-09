package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.dto.BzSearchDTO;
import com.schedule.supervisory.entity.Instruction;
import com.schedule.supervisory.entity.InstructionReply;

import java.util.List;

public interface IInstructionService extends IService<Instruction> {
    int insert(Instruction instruction);

    /**
     * 根据任务ID获取批示列表
     *
     * @param bzSearchDTO 任务ID
     * @return 批示列表
     */
    List<Instruction> getInstructionsByTaskId(BzSearchDTO bzSearchDTO);


}