package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.entity.BzIssueTargetRecord;

import java.util.List;

public interface IBzIssueTargetRecordService extends IService<BzIssueTargetRecord> {
    Long insertBzIssueTargetRecord(BzIssueTargetRecord bzIssueTargetRecord);

    List<BzIssueTargetRecord> getByTargetId(Integer targetId);
}