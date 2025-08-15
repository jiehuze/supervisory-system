package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.ConsultationMapper;
import com.schedule.supervisory.entity.Consultation;
import com.schedule.supervisory.entity.Task;
import com.schedule.supervisory.service.IConsultationService;
import org.springframework.stereotype.Service;

@Service
public class ConsultationServiceImpl extends ServiceImpl<ConsultationMapper, Consultation> implements IConsultationService {

    @Override
    public IPage<Consultation> listConsultations(IPage<Consultation> page) {
        return this.page(page);
    }

    @Override
    public IPage<Consultation> queryConsultByConditions(Consultation consultation, int pageNum, int pageSize) {
        Page<Consultation> page = new Page<>(pageNum, pageSize);

        // 构建查询条件
        LambdaQueryWrapper<Consultation> queryWrapper = new LambdaQueryWrapper<>();

        if (consultation.getId() != null) {
            queryWrapper.eq(Consultation::getId, consultation.getId());
        }
        if (consultation.getContent() != null) {
            queryWrapper.like(Consultation::getContent, consultation.getContent());
        }
        if (consultation.getPerson() != null) {
            queryWrapper.like(Consultation::getPerson, consultation.getPerson());
        }
        if (consultation.getPhone() != null) {
            queryWrapper.eq(Consultation::getPhone, consultation.getPhone());
        }
        if (consultation.getLeadingDepartment() != null) {
            queryWrapper.like(Consultation::getLeadingDepartment, consultation.getLeadingDepartment());
        }

        return page(page, queryWrapper);
    }
}