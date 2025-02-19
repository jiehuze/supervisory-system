package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.BzIssueMapper;
import com.schedule.supervisory.dto.BzFromTargetNameCount;
import com.schedule.supervisory.dto.BzSearchDTO;
import com.schedule.supervisory.dto.EffectiveGearCount;
import com.schedule.supervisory.entity.BzForm;
import com.schedule.supervisory.entity.BzIssue;
import com.schedule.supervisory.service.IBzIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class BzIssueServiceImpl extends ServiceImpl<BzIssueMapper, BzIssue> implements IBzIssueService {

    @Autowired
    private BzIssueMapper bzIssueMapper;

    @Override
    public boolean addBzIssue(BzIssue bzIssue) {
        return this.save(bzIssue);
    }

    @Override
    public Long insertBzIssue(BzIssue bzIssue) {
        boolean result = save(bzIssue);
        if (result) {
            return bzIssue.getId();
        } else {
            return null;
        }
    }

    @Override
    public IPage<BzIssue> getBzIssueByConditions(BzSearchDTO queryBzIssue, int pageNum, int pageSize) {
        //todo 读取用户的权限，根据权限判断要读取什么样的数据
        //权限有如下几种：1：承办人，只需要查看本单位下的数据；2：交办人：只需要看本人下的数据；3：承办领导：本部门及下属部门  4：领导：可以看到所有
        //1）交办人只读取自己创建的任务；2）承办人：只看自己负责的任务；3）交办领导：只看自己负责的部门；4）承包领导：只看自己负责的部门
        //所以看获取的人员部门数组；如果数组为空：判断创建人或者责任人；如果不为空，需要查询包含部门的数据
        // 创建分页对象
        Page<BzIssue> page = new Page<>(pageNum, pageSize);

        // 构建查询条件
        LambdaQueryWrapper<BzIssue> queryWrapper = new LambdaQueryWrapper<>();
//        if (queryBzIssue.getName() != null && !queryBzIssue.getName().isEmpty()) {
//            queryWrapper.like(BzIssue::getName, queryBzIssue.getName());
//        }

        if (queryBzIssue.getTypeId() != null) {
            queryWrapper.eq(BzIssue::getTypeId, queryBzIssue.getTypeId());
        }

        if (queryBzIssue.getPredictedGear() != null) {
            queryWrapper.eq(BzIssue::getPredictedGear, queryBzIssue.getPredictedGear());
        }

        if (queryBzIssue.getActualGear() != null) {
            queryWrapper.eq(BzIssue::getActualGear, queryBzIssue.getActualGear());
        }

        // 添加创建时间范围的筛选条件
        if (queryBzIssue.getCreatedAtStart() != null && queryBzIssue.getCreatedAtEnd() != null) {
            queryWrapper.between(BzIssue::getCreatedAt, queryBzIssue.getCreatedAtStart(), queryBzIssue.getCreatedAtEnd());
        }

        if (queryBzIssue.getDateType() != null) {
            queryWrapper.eq(BzIssue::getDateType, queryBzIssue.getDateType());
            if (queryBzIssue.getYear() != null) {
                queryWrapper.eq(BzIssue::getYear, queryBzIssue.getYear());
            }
            if (queryBzIssue.getQuarter() != null) {
                queryWrapper.eq(BzIssue::getQuarter, queryBzIssue.getQuarter());
            }
        }

        queryWrapper.orderByDesc(BzIssue::getId);

        return page(page, queryWrapper);
    }

    @Override
    public List<BzIssue> getGearsByConditions(BzSearchDTO queryBzIssue) {
        LambdaQueryWrapper<BzIssue> queryWrapper = new LambdaQueryWrapper<>();
        if (queryBzIssue.getDateType() != null) {
            queryWrapper.eq(BzIssue::getDateType, queryBzIssue.getDateType());
            if (queryBzIssue.getYear() != null) {
                queryWrapper.eq(BzIssue::getYear, queryBzIssue.getYear());
            }
            if (queryBzIssue.getQuarter() != null) {
                queryWrapper.eq(BzIssue::getQuarter, queryBzIssue.getQuarter());
            }
        }
        return list(queryWrapper);
    }

    @Override
    public long countBzIssue(BzIssue queryBzIssue) {
        LambdaQueryWrapper<BzIssue> queryWrapper = new LambdaQueryWrapper<>();
        if (queryBzIssue.getTypeId() != null) {
            queryWrapper.eq(BzIssue::getTypeId, queryBzIssue.getTypeId());
        } else {
//            return -1;
        }
        //如果为1
        if (queryBzIssue.getDateType() == 1) {
            queryWrapper.eq(BzIssue::getDateType, queryBzIssue.getDateType());
            queryWrapper.eq(BzIssue::getYear, queryBzIssue.getYear());
        } else if (queryBzIssue.getDateType() == 2) {
            queryWrapper.eq(BzIssue::getDateType, queryBzIssue.getDateType());
            queryWrapper.eq(BzIssue::getYear, queryBzIssue.getYear());
            queryWrapper.eq(BzIssue::getQuarter, queryBzIssue.getQuarter());
        }

        return count(queryWrapper);
    }

    @Override
    public boolean updateBzIssue(BzIssue bzIssue) {
        LambdaUpdateWrapper<BzIssue> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BzIssue::getId, bzIssue.getId())
                .set(BzIssue::getActualGear, bzIssue.getActualGear())
                .set(BzIssue::getPredictedGear, bzIssue.getPredictedGear())
                .set(BzIssue::getType, bzIssue.getType())
//                .set(BzIssue::getName, bzIssue.getName())
                .set(BzIssue::getFillCycle, bzIssue.getFillCycle())
                .set(BzIssue::getDateType, bzIssue.getDateType())
                .set(BzIssue::getYear, bzIssue.getYear())
                .set(BzIssue::getQuarter, bzIssue.getQuarter())
                .set(BzIssue::getTypeId, bzIssue.getTypeId());
        return update(updateWrapper);
    }

    @Override
    public List<Map<String, Object>> countEffectiveGear() {
        return bzIssueMapper.countEffectiveGear();
    }

    @Override
    public List<EffectiveGearCount> countGearCollect() {
        return bzIssueMapper.countGearCollect();
    }

    @Override
    public List<EffectiveGearCount> countGearCollectTargetByDate(LocalDateTime startTime, LocalDateTime endTime) {
        return bzIssueMapper.countGearCollectTargetByDate(startTime, endTime);
    }

    @Override
    public List<EffectiveGearCount> countGearCollectByDate(LocalDateTime startTime, LocalDateTime endTime) {
        return bzIssueMapper.countGearCollectByDate(startTime, endTime);
    }

    @Override
    public List<BzFromTargetNameCount> selectByTimeAndGear(LocalDateTime startTime, LocalDateTime endTime, Integer gear) {
        return bzIssueMapper.selectByTimeAndGear(startTime, endTime, gear);
    }
}