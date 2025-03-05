package com.schedule.supervisory.controller;

import com.schedule.common.BaseResponse;
import com.schedule.supervisory.entity.StageNode;
import com.schedule.supervisory.service.IStageNodeService;
import com.schedule.supervisory.service.ITaskService;
import com.schedule.utils.util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stage-nodes")
public class StageNodeController {

    @Autowired
    private IStageNodeService stageNodeService;

    @Autowired
    private ITaskService taskService;

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
        //修改阶段性状态时，需要更新延期时间
        if (status == 2 || status == 4) {
            StageNode stageNode = stageNodeService.getById(id);
            if (stageNode != null && util.daysDifference(stageNode.getDeadline()) > 0) {
                List<StageNode> stageNodeForOverdues = stageNodeService.getStageNodeForOverdue(stageNode.getTaskId().longValue());
                long taskoverdueDays = 0;
                for (StageNode sn : stageNodeForOverdues) {
                    taskoverdueDays = Math.max(util.daysDifference(sn.getDeadline()), taskoverdueDays);
                }
                if (taskoverdueDays > 0) {
                    taskService.updateOverdueDays(stageNode.getTaskId().longValue(), (int) taskoverdueDays);
                }
            }
        }

        return new BaseResponse(HttpStatus.OK.value(), "success", isUpdate, Integer.toString(0));
    }
}