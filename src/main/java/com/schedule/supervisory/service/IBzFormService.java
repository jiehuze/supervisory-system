package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.entity.BzForm;

import java.util.List;
import java.util.Map;

public interface IBzFormService extends IService<BzForm> {
    boolean addBzForm(BzForm bzForm);

    Long insertBzForm(BzForm bzForm);

    IPage<BzForm> getBzFormByConditions(BzForm queryBzform, int pageNum, int pageSize);

    boolean updateBzFrom(BzForm bzForm);

    List<Map<String, Object>> countEffectiveGear();
}