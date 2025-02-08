package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.entity.BzForm;

public interface IBzFormService extends IService<BzForm> {
    boolean addBzForm(BzForm bzForm);
}