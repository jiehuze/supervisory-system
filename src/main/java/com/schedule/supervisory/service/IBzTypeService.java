package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.entity.BzInstruction;
import com.schedule.supervisory.entity.BzType;

import java.util.List;

public interface IBzTypeService extends IService<BzType> {
    // 如果需要额外的服务方法，可以在这里定义
    List<BzType> getBzTypeByContains(BzType bzType);
    boolean deleteById(Long id);
}