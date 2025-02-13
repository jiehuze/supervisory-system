package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.entity.Membership;
import com.schedule.supervisory.entity.Task;

public interface IMembershipService extends IService<Membership> {
    boolean addOrUpdateMembership(Membership membership); // 新增或更新的方法声明

    boolean addMembership(Membership membership);

    boolean updateMembership(Membership membership);

    Membership getByLeadingDepartmentId(String leadingDepartmentId);
}