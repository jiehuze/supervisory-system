package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.entity.Check;

public interface ICheckService extends IService<Check> {
    boolean checkStatus(Check check);

    Check getByOnlyId(Check check);
}