package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.BzTypeMapper;
import com.schedule.supervisory.entity.BzType;
import com.schedule.supervisory.service.IBzTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BzTypeServiceImpl extends ServiceImpl<BzTypeMapper, BzType> implements IBzTypeService {
    @Override
    public List<BzType> getBzTypeByContains(BzType bzType) {
        LambdaQueryWrapper<BzType> queryWrapper = new LambdaQueryWrapper<>();
        if (bzType.getType() != null) {
            queryWrapper.eq(BzType::getType, bzType.getType()); // 根据taskId进行查询
        }
        queryWrapper.eq(BzType::isDelete, bzType.isDelete());
        queryWrapper.orderByAsc(BzType::getOrderNum);
        return this.list(queryWrapper);
    }

    @Override
    public boolean deleteById(Long id) {
        LambdaUpdateWrapper<BzType> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BzType::getId, id);
        updateWrapper.set(BzType::getOrderNum, 1000);
        updateWrapper.set(BzType::isDelete, true);
        return update(updateWrapper);
    }

    @Override
    public Integer getMaxTypeId(String type) {
        LambdaQueryWrapper<BzType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BzType::getType, type)
                .orderByDesc(BzType::getTypeId)
                .last("LIMIT 1");

        BzType bzType = getOne(queryWrapper);

        return bzType != null ? bzType.getTypeId() : null;
    }
    // 由于 ServiceImpl 已经实现了大部分基础功能，通常不需要在此添加额外逻辑
}