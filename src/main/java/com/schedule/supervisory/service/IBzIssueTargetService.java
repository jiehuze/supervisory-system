package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.entity.BzFormTarget;
import com.schedule.supervisory.entity.BzIssueTarget;

import java.util.List;

public interface IBzIssueTargetService extends IService<BzIssueTarget> {
    boolean updateProgressById(BzIssueTarget bzIssueTarget);

    boolean updateProgress(BzIssueTarget bzIssueTarget);

    boolean reviewProgress(BzIssueTarget bzIssueTarget);

    List<BzIssueTarget> getByIssueId(Long issueId);
}