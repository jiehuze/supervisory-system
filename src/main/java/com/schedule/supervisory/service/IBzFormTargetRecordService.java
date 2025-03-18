package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.entity.BzFormTargetRecord;

import java.util.List;

public interface IBzFormTargetRecordService extends IService<BzFormTargetRecord> {
    Long insertBzFormTargetRecord(BzFormTargetRecord bzFormTargetRecord);

    List<BzFormTargetRecord> getByTargetId(Integer targetId);

    boolean updateStatus(Long id, Integer status);
}