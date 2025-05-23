package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.dto.BzSearchDTO;
import com.schedule.supervisory.dto.DeptDTO;
import com.schedule.supervisory.entity.BzFormTarget;

import java.util.List;

public interface IBzFormTargetService extends IService<BzFormTarget> {
    boolean updateProgressById(BzFormTarget bzFormTarget);

    boolean updateProgress(BzFormTarget bzFormTarget);

    boolean reviewProgress(BzFormTarget bzFormTarget);

    boolean updateCheckById(Long id, Integer addStatus, Integer removeStatus);

    boolean clearCheckUserById(Long id);

    void updateCheckProcess(Long id, String processInstanceId, String processInstanceReviewIds);

    List<BzFormTarget> getByFormId(BzSearchDTO bzSearchDTO, List<DeptDTO> deptDTOs);

    List<BzFormTarget> getCheckTargetByFormId(BzSearchDTO bzSearchDTO, List<DeptDTO> deptDTOs);
}