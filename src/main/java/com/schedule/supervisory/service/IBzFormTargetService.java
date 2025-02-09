package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.entity.BzFormTarget;

import java.util.List;

public interface IBzFormTargetService extends IService<BzFormTarget> {
    boolean updateProgress(BzFormTarget bzFormTarget);

    boolean reviewProgress(BzFormTarget bzFormTarget);

    List<BzFormTarget> getByFormId(Long formId);
}