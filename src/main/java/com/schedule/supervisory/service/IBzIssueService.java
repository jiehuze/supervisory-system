package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.dto.BzFromTargetNameCount;
import com.schedule.supervisory.dto.BzSearchDTO;
import com.schedule.supervisory.dto.DeptDTO;
import com.schedule.supervisory.dto.EffectiveGearCount;
import com.schedule.supervisory.entity.BzIssue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IBzIssueService extends IService<BzIssue> {
    boolean addBzIssue(BzIssue bzIssue);

    Integer insertBzIssue(BzIssue bzIssue);

    IPage<BzIssue> getBzIssueByConditions(BzSearchDTO queryBzIssue, int pageNum, int pageSize, List<DeptDTO> deptDTOs);

    IPage<BzIssue> getBzIssueByConditions2(BzSearchDTO queryBzIssue, int pageNum, int pageSize, List<DeptDTO> deptDTOs);

    List<BzIssue> getGearsByConditions(BzSearchDTO queryBzIssue);

    public long countBzIssue(BzIssue queryBzIssue);

    boolean updateBzIssue(BzIssue bzIssue);

    boolean updateCheckById(Long taskId, Integer addStatus, Integer removeStatus);

    void updateCheckProcess(Long id, String processInstanceId, String processInstanceReviewIds);

    boolean clearCheckUserById(Long id);

    List<Map<String, Object>> countEffectiveGear();

    List<EffectiveGearCount> countGearCollect();

    List<EffectiveGearCount> countGearCollectTargetByDate(LocalDateTime startTime, LocalDateTime endTime);

    List<EffectiveGearCount> countGearCollectByDate(LocalDateTime startTime, LocalDateTime endTime);

    List<BzFromTargetNameCount> selectByTimeAndGear(LocalDateTime startTime, LocalDateTime endTime, Integer gear);
}