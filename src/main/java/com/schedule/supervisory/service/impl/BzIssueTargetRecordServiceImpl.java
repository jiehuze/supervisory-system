package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.BzIssueTargetRecordMapper;
import com.schedule.supervisory.entity.BzIssueTargetRecord;
import com.schedule.supervisory.service.IBzIssueTargetRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BzIssueTargetRecordServiceImpl extends ServiceImpl<BzIssueTargetRecordMapper, BzIssueTargetRecord> implements IBzIssueTargetRecordService {

    @Override
    public Long insertBzIssueTargetRecord(BzIssueTargetRecord bzIssueTargetRecord) {
        boolean result = save(bzIssueTargetRecord);
        if (result) {
            return bzIssueTargetRecord.getId();
        } else {
            return null;
        }
    }

    @Override
    public List<BzIssueTargetRecord> getByTargetId(Integer targetId) {
        LambdaQueryWrapper<BzIssueTargetRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BzIssueTargetRecord::getTargetId, targetId);
        return list(queryWrapper);
    }
}