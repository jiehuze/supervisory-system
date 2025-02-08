package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.BzFormMapper;
import com.schedule.supervisory.entity.BzForm;
import com.schedule.supervisory.service.IBzFormService;
import org.springframework.stereotype.Service;

@Service
public class BzFormServiceImpl extends ServiceImpl<BzFormMapper, BzForm> implements IBzFormService {
    @Override
    public boolean addBzForm(BzForm bzForm) {
        return this.save(bzForm);
    }
}