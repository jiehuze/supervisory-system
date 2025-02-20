package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.entity.BzInstruction;

import java.util.List;

public interface IBzInstructionService extends IService<BzInstruction> {
    /**
     * 根据任务ID获取批示列表
     *
     * @param bzInstruction 任务ID
     * @return 批示列表
     */
    List<BzInstruction> getInstructionsByContains(BzInstruction bzInstruction);
}
