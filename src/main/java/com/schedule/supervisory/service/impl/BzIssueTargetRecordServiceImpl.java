package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.BzIssueTargetRecordMapper;
import com.schedule.supervisory.entity.BzIssue;
import com.schedule.supervisory.entity.BzIssueTargetRecord;
import com.schedule.supervisory.service.IBzIssueTargetRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BzIssueTargetRecordServiceImpl extends ServiceImpl<BzIssueTargetRecordMapper, BzIssueTargetRecord> implements IBzIssueTargetRecordService {

    @Autowired
    private BzIssueTargetRecordMapper bzIssueTargetRecordMapper;

    @Override
    public int insertBzIssueTargetRecord(BzIssueTargetRecord bzIssueTargetRecord) {
        return bzIssueTargetRecordMapper.insert(bzIssueTargetRecord);
//        boolean result = save(bzIssueTargetRecord);
//        if (result) {
//            return bzIssueTargetRecord.getId();
//        } else {
//            return null;
//        }
    }

    @Override
    public List<BzIssueTargetRecord> getByTargetId(Integer targetId) {
        LambdaQueryWrapper<BzIssueTargetRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BzIssueTargetRecord::getTargetId, targetId);
        queryWrapper.orderByDesc(BzIssueTargetRecord::getId);
        return list(queryWrapper);
    }

    @Override
    public List<BzIssueTargetRecord> getHistoryByTargetId(Integer targetId) {
        return bzIssueTargetRecordMapper.selectDistinctPredictedGearRecords(targetId);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        LambdaQueryWrapper<BzIssueTargetRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BzIssueTargetRecord::getTargetId, id)
                .orderByDesc(BzIssueTargetRecord::getId)
                .last("LIMIT 1");
        BzIssueTargetRecord one = getOne(queryWrapper);
        if (one == null)
            return false;


        LambdaUpdateWrapper<BzIssueTargetRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BzIssueTargetRecord::getId, one.getId());
        updateWrapper.set(BzIssueTargetRecord::getStatus, status);

        return update(updateWrapper);
    }
}