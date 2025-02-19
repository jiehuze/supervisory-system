package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.dto.BzFromTargetNameCount;
import com.schedule.supervisory.dto.BzSearchDTO;
import com.schedule.supervisory.dto.EffectiveGearCount;
import com.schedule.supervisory.entity.BzIssue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IBzIssueService extends IService<BzIssue> {
    boolean addBzIssue(BzIssue bzIssue);

    Long insertBzIssue(BzIssue bzIssue);

    IPage<BzIssue> getBzIssueByConditions(BzSearchDTO queryBzIssue, int pageNum, int pageSize);

    List<BzIssue> getGearsByConditions(BzSearchDTO queryBzIssue);

    public long countBzIssue(BzIssue queryBzIssue);

    boolean updateBzIssue(BzIssue bzIssue);

    List<Map<String, Object>> countEffectiveGear();

    List<EffectiveGearCount> countGearCollect();

    List<EffectiveGearCount> countGearCollectTargetByDate(LocalDateTime startTime, LocalDateTime endTime);

    List<EffectiveGearCount> countGearCollectByDate(LocalDateTime startTime, LocalDateTime endTime);

    List<BzFromTargetNameCount> selectByTimeAndGear(LocalDateTime startTime, LocalDateTime endTime, Integer gear);
}