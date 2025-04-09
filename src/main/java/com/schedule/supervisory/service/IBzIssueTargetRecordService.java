package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.entity.BzIssueTargetRecord;

import java.util.List;

public interface IBzIssueTargetRecordService extends IService<BzIssueTargetRecord> {
    int insertBzIssueTargetRecord(BzIssueTargetRecord bzIssueTargetRecord);

    List<BzIssueTargetRecord> getByTargetId(Integer targetId);

    List<BzIssueTargetRecord> getHistoryByTargetId(Integer targetId);

    boolean updateStatus(Long id, Integer status);
}