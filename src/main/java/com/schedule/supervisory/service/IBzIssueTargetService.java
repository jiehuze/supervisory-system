package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.dto.BzSearchDTO;
import com.schedule.supervisory.dto.DeptDTO;
import com.schedule.supervisory.entity.BzFormTarget;
import com.schedule.supervisory.entity.BzIssueTarget;

import java.util.List;

public interface IBzIssueTargetService extends IService<BzIssueTarget> {
    boolean updateProgressById(BzIssueTarget bzIssueTarget);

    boolean updateProgress(BzIssueTarget bzIssueTarget);

    boolean updateCheckById(Long id, Integer addStatus, Integer removeStatus);

    boolean reviewProgress(BzIssueTarget bzIssueTarget);

    List<BzIssueTarget> getByIssueId(BzSearchDTO bzSearchDTO, List<DeptDTO> deptDTOs);
}