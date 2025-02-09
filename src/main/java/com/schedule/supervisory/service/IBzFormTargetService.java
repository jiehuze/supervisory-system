package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.entity.BzFormTarget;

public interface IBzFormTargetService extends IService<BzFormTarget> {
    boolean updateProgress(BzFormTarget bzFormTarget);

    boolean reviewProgress(BzFormTarget bzFormTarget);
}