package com.schedule.supervisory.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.schedule.common.BaseResponse;
import com.schedule.supervisory.dto.TaskCollectDTO;
import com.schedule.supervisory.dto.TaskDetailDTO;
import com.schedule.supervisory.dto.TaskSearchDTO;
import com.schedule.supervisory.entity.*;
import com.schedule.supervisory.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/external")
public class ExternalController {

    @Autowired
    private ITaskService taskService;
    @Autowired
    private IStageNodeService stageNodeService;
    @Autowired
    private IProgressReportService progressReportService;

    @Autowired
    private IExternalTaskService externalTaskService;

    @Autowired
    private IYkbMessageService ykbMessageService;

    @Autowired
    private IConsultationService consultationService;

    // 新增任务
    @PutMapping("/submit")
    public BaseResponse createExternalTask(@RequestBody ExternalTask externalTask) {
        Long id = externalTaskService.addExternalTask(externalTask);
        ykbMessageService.sendMessageForExternal(externalTask);
        return new BaseResponse(HttpStatus.OK.value(), "success", externalTask, Integer.toString(0));
    }

    @PutMapping("/consult")
    public BaseResponse createExternalConsult(@RequestBody Consultation consultation) {
        boolean save = consultationService.save(consultation);
        ykbMessageService.sendMessageForConsult(consultation);
        return new BaseResponse(HttpStatus.OK.value(), "success", save, Integer.toString(0));
    }

    // 分页查询
    @GetMapping("/consult/search")
    public BaseResponse getAllConsult(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestHeader(value = "tenant-id", required = false) String tenantId, @ModelAttribute Consultation queryConsult, @RequestParam(value = "current", defaultValue = "1") Integer pageNum, @RequestParam(value = "size", defaultValue = "10") Integer pageSize) {
        IPage<Consultation> consultationIPage = consultationService.queryConsultByConditions(queryConsult, pageNum, pageSize);
        return new BaseResponse(HttpStatus.OK.value(), "success", consultationIPage, Integer.toString(0));
    }

    // 分页查询
    @GetMapping("/search")
    public BaseResponse getAllExternalTasks(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestHeader(value = "tenant-id", required = false) String tenantId, @RequestParam(value = "current", defaultValue = "1") Integer pageNum, @RequestParam(value = "size", defaultValue = "10") Integer pageSize) {
        IPage<ExternalTask> externalTasks = externalTaskService.getExternalTasks(pageNum, pageSize);
        return new BaseResponse(HttpStatus.OK.value(), "success", externalTasks, Integer.toString(0));
    }

    @GetMapping("/{id}")
    public BaseResponse getTask(@PathVariable Long id) {
        ExternalTask externalTask = externalTaskService.getById(id);
        return new BaseResponse(HttpStatus.OK.value(), "success", externalTask, Integer.toString(0));
    }

    @GetMapping("/task/detail")
    public BaseResponse getTask(@ModelAttribute TaskSearchDTO queryTask) {
        queryTask.setTaskType(0);
        String content = queryTask.getContent();

        if (content == null || content.length() <= 0) {
            return new BaseResponse(HttpStatus.OK.value(), "success", null, Integer.toString(0));
        }

        List<TaskDetailDTO> taskDetailDTOS = new ArrayList<>();
        List<Task> tasksBySearchDTO = taskService.getTasksBySearchDTO(queryTask);
        for (Task task : tasksBySearchDTO) {
            TaskDetailDTO taskDetail = new TaskDetailDTO();
            Long taskId = task.getId();
            List<StageNode> stageNodesByTaskId = stageNodeService.getStageNodesByTaskId(taskId.intValue());
            List<ProgressReport> progressReportsByTaskId = progressReportService.getProgressReportsByTaskId(taskId, null);
            taskDetail.setTask(task);
            taskDetail.setStageNodes(stageNodesByTaskId);
            taskDetail.setProgressReports(progressReportsByTaskId);

            taskDetailDTOS.add(taskDetail);
        }

        return new BaseResponse(HttpStatus.OK.value(), "success", taskDetailDTOS, Integer.toString(0));
    }
}
