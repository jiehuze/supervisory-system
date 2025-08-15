package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.entity.ExternalTask;

public interface IExternalTaskService extends IService<ExternalTask> {
    IPage<ExternalTask> getExternalTasks(int pageNum, int pageSize);

    Long addExternalTask(ExternalTask externalTask);
}