package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.entity.Instruction;

import java.util.List;

public interface IInstructionService extends IService<Instruction> {
    /**
     * 根据任务ID获取批示列表
     * @param taskId 任务ID
     * @return 批示列表
     */
    List<Instruction> getInstructionsByTaskId(Integer taskId);
}