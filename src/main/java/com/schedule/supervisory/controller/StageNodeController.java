package com.schedule.supervisory.controller;

import com.schedule.common.BaseResponse;
import com.schedule.supervisory.entity.StageNode;
import com.schedule.supervisory.service.IStageNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stage-nodes")
public class StageNodeController {

    @Autowired
    private IStageNodeService stageNodeService;

    @GetMapping("/task/{taskId}")
    public BaseResponse getStageNodesByTaskId(@PathVariable Integer taskId) {
        List<StageNode> stageNodes = stageNodeService.getStageNodesByTaskId(taskId);

        return new BaseResponse(HttpStatus.OK.value(), "success", stageNodes, Integer.toString(0));
    }

//    @PostMapping("/add")
//    public StageNode addStageNode(@RequestBody StageNode stageNode) {
//        return stageNodeService.createStageNode(stageNode);
//    }

    @PostMapping("/add")
    public BaseResponse batchAddStageNodes(@RequestBody List<StageNode> stageNodes) {
        boolean isSuccess = stageNodeService.batchCreateStageNodes(stageNodes);

        return new BaseResponse(HttpStatus.OK.value(), "success", isSuccess, Integer.toString(0));
    }

    @PutMapping("/{id}/status")
    public BaseResponse updateStatus(@PathVariable Integer id, @RequestParam Integer status) {
        boolean isUpdate = stageNodeService.updateStatusById(id, status);

        return new BaseResponse(HttpStatus.OK.value(), "success", isUpdate, Integer.toString(0));
    }
}