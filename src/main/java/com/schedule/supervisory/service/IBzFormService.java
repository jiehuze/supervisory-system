package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.dto.BzFromTargetNameCount;
import com.schedule.supervisory.dto.BzSearchDTO;
import com.schedule.supervisory.dto.DeptDTO;
import com.schedule.supervisory.dto.EffectiveGearCount;
import com.schedule.supervisory.entity.BzForm;
import com.schedule.supervisory.entity.BzFormTarget;
import com.schedule.supervisory.entity.BzIssue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IBzFormService extends IService<BzForm> {
    boolean addBzForm(BzForm bzForm);

    Integer insertBzForm(BzForm bzForm);

    IPage<BzForm> getBzFormByConditions(BzSearchDTO queryBzForm, int pageNum, int pageSize, List<DeptDTO> deptDTOs);

    IPage<BzForm> getBzFormByConditions2(BzSearchDTO queryBzfForm, int pageNum, int pageSize, List<DeptDTO> deptDTOs);

    List<BzForm> getGearsByConditions(BzSearchDTO queryBzSearch);

    long countBzForm(BzForm queryBzForm);

    boolean updateBzFrom(BzForm bzForm);

    boolean updateCheckById(Long taskId, Integer addStatus, Integer removeStatus);

    boolean clearCheckUserById(Long id);

    void updateCheckProcess(Long id, String processInstanceId, String processInstanceReviewIds);

    List<Map<String, Object>> countEffectiveGear();

    List<EffectiveGearCount> countGearCollect();

    List<EffectiveGearCount> countGearCollectTargetByDate(LocalDateTime startTime, LocalDateTime endTime);

    List<EffectiveGearCount> countGearCollectByDate(LocalDateTime startTime, LocalDateTime endTime);

    List<BzFromTargetNameCount> selectByTimeAndGear(LocalDateTime startTime, LocalDateTime endTime, Integer gear, Integer typeId);

    IPage<BzFormTarget> selectByTypeAndGear(int pageNum, int pageSize, LocalDateTime startTime, LocalDateTime endTime, Integer gear, Integer typeId);
}