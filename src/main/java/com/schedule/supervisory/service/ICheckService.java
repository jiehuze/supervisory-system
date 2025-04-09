package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.entity.Check;
import org.springframework.scheduling.annotation.Async;

public interface ICheckService extends IService<Check> {
    int insertCheck(Check check);

    boolean checkStatus(Check check);

    Check getByOnlyId(Check check);

    boolean updateCheckStatusByCheckType(Check check);

    Check getByProcessInstanceId(String processInstanceId);

    boolean updateCheckInfoToTarget(Check check);

    @Async
    void executeAfterDelay(String url, String authorizationHeader, String tenantId, Check check) throws InterruptedException;
}