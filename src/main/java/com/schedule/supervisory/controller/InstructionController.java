package com.schedule.supervisory.controller;

import com.schedule.common.BaseResponse;
import com.schedule.supervisory.dto.BzSearchDTO;
import com.schedule.supervisory.dto.InstructionResponse;
import com.schedule.supervisory.entity.BzInstruction;
import com.schedule.supervisory.entity.Instruction;
import com.schedule.supervisory.entity.InstructionReply;
import com.schedule.supervisory.entity.Task;
import com.schedule.supervisory.service.IInstructionReplyService;
import com.schedule.supervisory.service.IInstructionService;
import com.schedule.supervisory.service.ITaskService;
import com.schedule.supervisory.service.IYkbMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/instructions")
public class InstructionController {

    @Autowired
    private IInstructionService instructionService;
    @Autowired
    private IInstructionReplyService instructionReplyService;
    @Autowired
    private ITaskService taskService;
    @Autowired
    private IYkbMessageService ykbMessageService;

    @GetMapping("/all")
    public BaseResponse getAllInstructions() {
        List<Instruction> list = instructionService.list();
        return new BaseResponse(HttpStatus.OK.value(), "success", list, Integer.toString(0));
    }

    @PostMapping("/add")
    public BaseResponse addInstruction(@RequestBody Instruction instruction) {
        int save = instructionService.insert(instruction);
        if (instruction.getTaskId() != null) {
            taskService.updateInstructionById((long) instruction.getTaskId(), instruction.getContent());

            Task messageTask = taskService.getById(instruction.getTaskId());
            ykbMessageService.sendMessageForInstruction(messageTask); //办结申请
        }

        return new BaseResponse(HttpStatus.OK.value(), "success", save, Integer.toString(0));
    }

    @PostMapping("/addReply")
    public BaseResponse addInstructionReply(@RequestBody InstructionReply instructionReply) {
        int save = instructionReplyService.addReply(instructionReply);
        return new BaseResponse(HttpStatus.OK.value(), "success", save, Integer.toString(0));
    }

    /**
     * 根据任务ID（通过URL参数传递）获取批示列表
     *
     * @param bzSearchDTO 任务ID，作为查询参数
     * @return 批示列表
     */
    @GetMapping
    public BaseResponse getInstructionsByTaskId(@ModelAttribute BzSearchDTO bzSearchDTO) {
        List<Instruction> instructions = instructionService.getInstructionsByTaskId(bzSearchDTO);
        return new BaseResponse(HttpStatus.OK.value(), "success", instructions, Integer.toString(0));
    }

    /**
     * 根据任务ID（通过URL参数传递）获取批示列表
     *
     * @param bzSearchDTO 任务ID，作为查询参数
     * @return 批示列表
     */
    @GetMapping("/searchAll")
    public BaseResponse getInstructions(@ModelAttribute BzSearchDTO bzSearchDTO) {
        // 如果提供了taskId，则根据taskId查询
        ArrayList<InstructionResponse> instructionResponses = new ArrayList<>();
        List<Instruction> instructions = instructionService.getInstructionsByTaskId(bzSearchDTO);

        for (Instruction instruction : instructions) {
            InstructionResponse instructionResponse = new InstructionResponse();
            instructionResponse.setInstruction(instruction);
            List<InstructionReply> repliesByInstructionId = instructionReplyService.getRepliesByInstructionId(instruction.getId());
            instructionResponse.setInstructionReplieList(repliesByInstructionId);

            instructionResponses.add(instructionResponse);
        }
        return new BaseResponse(HttpStatus.OK.value(), "success", instructionResponses, Integer.toString(0));
    }
}