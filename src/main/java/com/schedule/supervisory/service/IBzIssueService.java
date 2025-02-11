package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.entity.BzIssue;

import java.util.List;
import java.util.Map;

public interface IBzIssueService extends IService<BzIssue> {
    boolean addBzIssue(BzIssue bzIssue);

    Long insertBzIssue(BzIssue bzIssue);

    IPage<BzIssue> getBzIssueByConditions(BzIssue queryBzIssue, int pageNum, int pageSize);

    boolean updateBzFrom(BzIssue bzIssue);

    List<Map<String, Object>> countEffectiveGear();
}