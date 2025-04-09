package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.entity.BzFormTargetRecord;

import java.util.List;

public interface IBzFormTargetRecordService extends IService<BzFormTargetRecord> {
    int insertBzFormTargetRecord(BzFormTargetRecord bzFormTargetRecord);

    List<BzFormTargetRecord> getByTargetId(Integer targetId);

    List<BzFormTargetRecord> getHistoryByTargetId(Integer targetId);

    boolean updateStatus(Long id, Integer status);
}