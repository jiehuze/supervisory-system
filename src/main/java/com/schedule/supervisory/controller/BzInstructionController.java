package com.schedule.supervisory.controller;

import com.schedule.common.BaseResponse;
import com.schedule.supervisory.entity.BzInstruction;
import com.schedule.supervisory.service.IBzInstructionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bzInstructions")
public class BzInstructionController {

    @Autowired
    private IBzInstructionService bzInstructionService;

    @GetMapping("/all")
    public BaseResponse getAllInstructions() {
        List<BzInstruction> list = bzInstructionService.list();
        return new BaseResponse(HttpStatus.OK.value(), "success", list, Integer.toString(0));
    }

    @PostMapping("/add")
    public BaseResponse addInstruction(@RequestBody BzInstruction bzInstruction) {
        boolean save = bzInstructionService.save(bzInstruction);
        return new BaseResponse(HttpStatus.OK.value(), "success", save, Integer.toString(0));
    }

    /**
     * 根据任务ID（通过URL参数传递）获取批示列表
     *
     * @param bzInstruction 任务ID，作为查询参数
     * @return 批示列表
     */
    @GetMapping
    public BaseResponse getInstructionsByTaskId(@ModelAttribute BzInstruction bzInstruction) {
        List<BzInstruction> bzInstructions = bzInstructionService.getInstructionsByContains(bzInstruction);

        return new BaseResponse(HttpStatus.OK.value(), "success", bzInstructions, Integer.toString(0));
    }


}
