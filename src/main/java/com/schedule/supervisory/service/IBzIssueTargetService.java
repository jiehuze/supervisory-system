package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.entity.BzIssueTarget;

import java.util.List;

public interface IBzIssueTargetService extends IService<BzIssueTarget> {
    boolean updateProgress(BzIssueTarget bzIssueTarget);

    boolean reviewProgress(BzIssueTarget bzIssueTarget);

    List<BzIssueTarget> getByIssueId(Long issueId);
}