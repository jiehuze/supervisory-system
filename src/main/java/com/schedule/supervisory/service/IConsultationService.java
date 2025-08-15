package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.entity.Consultation;

public interface IConsultationService extends IService<Consultation> {
    IPage<Consultation> listConsultations(IPage<Consultation> page);

    IPage<Consultation> queryConsultByConditions(Consultation consultation, int pageNum, int pageSize);
}