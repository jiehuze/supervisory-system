package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.MembershipMapper;
import com.schedule.supervisory.entity.Membership;
import com.schedule.supervisory.service.IMembershipService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembershipServiceImpl extends ServiceImpl<MembershipMapper, Membership> implements IMembershipService {
    @Override
    public boolean addOrUpdateMembership(Membership membership) {
        Membership deparment = getByLeadingDepartmentId(membership.getLeadingDepartmentId());
        if (deparment == null) {
            return this.save(membership); // 插入新记录
        } else {
            membership.setId(deparment.getId());
            return this.updateById(membership);
        }
    }

    @Override
    public boolean addMembership(Membership membership) {
        return this.save(membership); // MyBatis-Plus 自动处理插入
    }

    @Override
    public boolean updateMembership(Membership membership) {
        LambdaQueryWrapper<Membership> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Membership::getPriority).last("LIMIT 1");
        Membership one = getOne(queryWrapper);
        membership.setPriority(one.getPriority() + 1);
        return this.updateById(membership); // MyBatis-Plus 自动处理更新
    }

    @Override
    public Membership getByLeadingDepartmentId(String leadingDepartmentId) {
        LambdaQueryWrapper<Membership> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Membership::getLeadingDepartmentId, leadingDepartmentId);
//                .orderByDesc(Membership::getPriority)
//                .last("LIMIT 1");
        return this.getOne(queryWrapper);
    }

    @Override
    public List<Membership> getListByLeadingDepartmentId(String leadingDepartmentId) {
        LambdaQueryWrapper<Membership> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Membership::getLeadingDepartmentId, leadingDepartmentId);
        queryWrapper.orderByDesc(Membership::getUpdatedAt);
//        queryWrapper.orderByDesc(Membership::getPriority);
        return list(queryWrapper);
    }
}