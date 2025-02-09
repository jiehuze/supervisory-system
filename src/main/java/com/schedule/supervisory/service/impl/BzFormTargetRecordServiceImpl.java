package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.BzFormTargetRecordMapper;
import com.schedule.supervisory.entity.BzFormTargetRecord;
import com.schedule.supervisory.service.IBzFormTargetRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BzFormTargetRecordServiceImpl extends ServiceImpl<BzFormTargetRecordMapper, BzFormTargetRecord> implements IBzFormTargetRecordService {

    @Override
    public Long insertBzFormTargetRecord(BzFormTargetRecord bzFormTargetRecord) {
        boolean result = save(bzFormTargetRecord);
        if (result) {
            return bzFormTargetRecord.getId();
        } else {
            return null;
        }
    }

    @Override
    public List<BzFormTargetRecord> getByTargetId(Integer targetId) {
        LambdaQueryWrapper<BzFormTargetRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BzFormTargetRecord::getTargetId, targetId);
        return list(queryWrapper);
    }
}