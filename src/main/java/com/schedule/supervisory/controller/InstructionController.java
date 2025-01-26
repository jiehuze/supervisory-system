package com.schedule.supervisory.controller;

import com.schedule.common.BaseResponse;
import com.schedule.supervisory.entity.Instruction;
import com.schedule.supervisory.service.IInstructionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/instructions")
public class InstructionController {

    @Autowired
    private IInstructionService instructionService;

    @GetMapping("/all")
    public BaseResponse getAllInstructions() {
        List<Instruction> list = instructionService.list();
        return new BaseResponse(HttpStatus.OK.value(), "success", list, Integer.toString(0));
    }

    @PostMapping("/add")
    public BaseResponse addInstruction(@RequestBody Instruction instruction) {
        boolean save = instructionService.save(instruction);
        return new BaseResponse(HttpStatus.OK.value(), "success", save, Integer.toString(0));
    }

    /**
     * 根据任务ID（通过URL参数传递）获取批示列表
     *
     * @param taskId 任务ID，作为查询参数
     * @return 批示列表
     */
    @GetMapping
    public BaseResponse getInstructionsByTaskId(@RequestParam(value = "taskId", required = false) Integer taskId) {
        // 如果提供了taskId，则根据taskId查询
        List<Instruction> instructions = instructionService.getInstructionsByTaskId(taskId);
        return new BaseResponse(HttpStatus.OK.value(), "success", instructions, Integer.toString(0));
    }
}