package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.BzFormTargetRecordMapper;
import com.schedule.supervisory.entity.BzFormTarget;
import com.schedule.supervisory.entity.BzFormTargetRecord;
import com.schedule.supervisory.entity.BzIssueTargetRecord;
import com.schedule.supervisory.service.IBzFormTargetRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BzFormTargetRecordServiceImpl extends ServiceImpl<BzFormTargetRecordMapper, BzFormTargetRecord> implements IBzFormTargetRecordService {

    @Autowired
    private BzFormTargetRecordMapper bzFormTargetRecordMapper;

    @Override
    public int insertBzFormTargetRecord(BzFormTargetRecord bzFormTargetRecord) {
        return bzFormTargetRecordMapper.insert(bzFormTargetRecord);
//        boolean result = save(bzFormTargetRecord);
//        if (result) {
//            return bzFormTargetRecord.getId();
//        } else {
//            return null;
//        }
    }

    @Override
    public List<BzFormTargetRecord> getByTargetId(Integer targetId) {
        LambdaQueryWrapper<BzFormTargetRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BzFormTargetRecord::getTargetId, targetId);
        queryWrapper.orderByDesc(BzFormTargetRecord::getId);
        return list(queryWrapper);
    }

    @Override
    public List<BzFormTargetRecord> getHistoryByTargetId(Integer targetId) {
        return bzFormTargetRecordMapper.selectDistinctPredictedGearRecords(targetId);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        LambdaQueryWrapper<BzFormTargetRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BzFormTargetRecord::getTargetId, id)
                .orderByDesc(BzFormTargetRecord::getId)
                .last("LIMIT 1");
        BzFormTargetRecord one = getOne(queryWrapper);
        if (one == null)
            return false;

        LambdaUpdateWrapper<BzFormTargetRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BzFormTargetRecord::getId, one.getId());
        updateWrapper.set(BzFormTargetRecord::getStatus, status);

        return update(updateWrapper);
    }
}