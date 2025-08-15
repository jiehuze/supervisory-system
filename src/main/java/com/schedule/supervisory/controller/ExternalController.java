package com.schedule.supervisory.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.schedule.common.BaseResponse;
import com.schedule.supervisory.entity.ExternalTask;
import com.schedule.supervisory.entity.Task;
import com.schedule.supervisory.service.IExternalTaskService;
import com.schedule.supervisory.service.IYkbMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/external")
public class ExternalController {
    @Autowired
    private IExternalTaskService externalTaskService;

    @Autowired
    private IYkbMessageService ykbMessageService;

    // 新增任务
    @PutMapping("/submit")
    public BaseResponse createExternalTask(@RequestBody ExternalTask externalTask) {
        Long id = externalTaskService.addExternalTask(externalTask);
        ykbMessageService.sendMessageForExternal(externalTask);
        return new BaseResponse(HttpStatus.OK.value(), "success", externalTask, Integer.toString(0));
    }

    // 分页查询
    @GetMapping("/search")
    public BaseResponse getAllExternalTasks(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestHeader(value = "tenant-id", required = false) String tenantId,
            @RequestParam(value = "current", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "size", defaultValue = "10") Integer pageSize) {
        IPage<ExternalTask> externalTasks = externalTaskService.getExternalTasks(pageNum, pageSize);
        return new BaseResponse(HttpStatus.OK.value(), "success", externalTasks, Integer.toString(0));
    }

    @GetMapping("/{id}")
    public BaseResponse getTask(@PathVariable Long id) {
        ExternalTask externalTask = externalTaskService.getById(id);
        return new BaseResponse(HttpStatus.OK.value(), "success", externalTask, Integer.toString(0));
    }
}
