package com.schedule.supervisory.controller;

import com.schedule.supervisory.dto.OrderVerCodeDTO;
import com.schedule.supervisory.entity.StageNode;
import com.schedule.supervisory.service.IStageNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stage-nodes")
public class StageNodeController {

    @Autowired
    private IStageNodeService stageNodeService;

    @GetMapping("/task/{taskId}")
    public List<StageNode> getStageNodesByTaskId(@PathVariable Integer taskId) {
        return stageNodeService.getStageNodesByTaskId(taskId);
    }

    @PostMapping("/add")
    public StageNode addStageNode(@RequestBody StageNode stageNode) {
        return stageNodeService.createStageNode(stageNode);
    }

    @PostMapping("/batch-add")
    public void batchAddStageNodes(@RequestBody List<StageNode> stageNodes) {
        stageNodeService.batchCreateStageNodes(stageNodes);
    }
}