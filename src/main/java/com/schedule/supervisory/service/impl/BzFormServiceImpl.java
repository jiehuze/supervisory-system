package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.BzFormMapper;
import com.schedule.supervisory.dto.BzFromTargetNameCount;
import com.schedule.supervisory.dto.BzSearchDTO;
import com.schedule.supervisory.dto.DeptDTO;
import com.schedule.supervisory.dto.EffectiveGearCount;
import com.schedule.supervisory.entity.BzForm;
import com.schedule.supervisory.entity.BzFormTarget;
import com.schedule.supervisory.entity.Task;
import com.schedule.supervisory.service.IBzFormService;
import com.schedule.utils.util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class BzFormServiceImpl extends ServiceImpl<BzFormMapper, BzForm> implements IBzFormService {
    @Autowired
    private BzFormMapper bzFormMapper;

    @Override
    public boolean addBzForm(BzForm bzForm) {
        return this.save(bzForm);
    }

    @Override
    public Long insertBzForm(BzForm bzForm) {
        boolean result = save(bzForm);
        if (result) {
            return bzForm.getId();
        } else {
            return null;
        }
    }

    @Override
    public IPage<BzForm> getBzFormByConditions(BzSearchDTO queryBzform, int pageNum, int pageSize, List<DeptDTO> deptDTOs) {
        //权限有如下几种：1：承办人，只需要查看本单位下的数据；2：交办人：只需要看本人下的数据；3：承办领导：本部门及下属部门  4：领导：可以看到所有
        //1）交办人只读取自己创建的任务；2）承办人：只看自己负责的任务；3）交办领导：只看自己负责的部门；4）承包领导：只看自己负责的部门
        //所以看获取的人员部门数组；如果数组为空：判断创建人或者责任人；如果不为空，需要查询包含部门的数据
        // 创建分页对象
        Page<BzForm> page = new Page<>(pageNum, pageSize);

        // 构建查询条件
        LambdaQueryWrapper<BzForm> queryWrapper = new LambdaQueryWrapper<>();
        if (queryBzform.getTypeId() != null) {
            queryWrapper.eq(BzForm::getTypeId, queryBzform.getTypeId());
        }

        if (queryBzform.getPredictedGear() != null) {
            queryWrapper.eq(BzForm::getPredictedGear, queryBzform.getPredictedGear());
        }

        if (queryBzform.getActualGear() != null) {
            queryWrapper.eq(BzForm::getActualGear, queryBzform.getActualGear());
        }

        // 添加创建时间范围的筛选条件
        if (queryBzform.getCreatedAtStart() != null && queryBzform.getCreatedAtEnd() != null) {
            queryWrapper.between(BzForm::getCreatedAt, queryBzform.getCreatedAtStart(), queryBzform.getCreatedAtEnd());
        }

        if (queryBzform.getDateType() != null) {
            queryWrapper.eq(BzForm::getDateType, queryBzform.getDateType());
            if (queryBzform.getYear() != null) {
                queryWrapper.eq(BzForm::getYear, queryBzform.getYear());
            }
            if (queryBzform.getQuarter() != null) {
                queryWrapper.eq(BzForm::getQuarter, queryBzform.getQuarter());
            }
        }

        // 处理leadingOfficialId模糊查询的情况
        queryWrapper.and(wrapper -> {
            if (queryBzform.getUserId() != null && !queryBzform.getUserId().isEmpty()) {
                wrapper.or(w -> w.like(BzForm::getAssignerId, queryBzform.getUserId()));
            }

            if (deptDTOs != null && deptDTOs.size() > 0) {
                for (DeptDTO deptDTO : deptDTOs) {
                    wrapper.or(w -> w.like(BzForm::getLeadingDepartmentId, deptDTO.getDeptId())); //牵头单位
                    wrapper.or(w -> w.like(BzForm::getResponsibleDeptId, deptDTO.getDeptId())); //责任单位
                }
            }
        });

//        queryWrapper.orderByDesc(BzForm::getId);
        queryWrapper.orderByAsc(BzForm::getTypeId);

        return page(page, queryWrapper);
    }

    @Override
    public List<BzForm> getGearsByConditions(BzSearchDTO queryBzSearch) {
        LambdaQueryWrapper<BzForm> queryWrapper = new LambdaQueryWrapper<>();
        if (queryBzSearch.getDateType() != null) {
            queryWrapper.eq(BzForm::getDateType, queryBzSearch.getDateType());
            if (queryBzSearch.getYear() != null) {
                queryWrapper.eq(BzForm::getYear, queryBzSearch.getYear());
            }
            if (queryBzSearch.getQuarter() != null) {
                queryWrapper.eq(BzForm::getQuarter, queryBzSearch.getQuarter());
            }
        }
        return list(queryWrapper);
    }

    @Override
    public long countBzForm(BzForm queryBzform) {
        LambdaQueryWrapper<BzForm> queryWrapper = new LambdaQueryWrapper<>();
        if (queryBzform.getTypeId() != null) {
            queryWrapper.eq(BzForm::getTypeId, queryBzform.getTypeId());
        } else {
//            return -1;
        }
        //如果为1
        if (queryBzform.getDateType() == 1) {
            queryWrapper.eq(BzForm::getDateType, queryBzform.getDateType());
            queryWrapper.eq(BzForm::getYear, queryBzform.getYear());
        } else if (queryBzform.getDateType() == 2) {
            queryWrapper.eq(BzForm::getDateType, queryBzform.getDateType());
            queryWrapper.eq(BzForm::getYear, queryBzform.getYear());
            queryWrapper.eq(BzForm::getQuarter, queryBzform.getQuarter());
        }

        return count(queryWrapper);
    }

    @Override
    public boolean updateBzFrom(BzForm bzForm) {
        LambdaUpdateWrapper<BzForm> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BzForm::getId, bzForm.getId())
                .set(BzForm::getActualGear, bzForm.getActualGear())
                .set(BzForm::getPredictedGear, bzForm.getPredictedGear())
                .set(BzForm::getOperator, bzForm.getOperator())
                .set(BzForm::getOperatorId, bzForm.getOperatorId())
                .set(BzForm::getType, bzForm.getType())
                .set(BzForm::getFillCycle, bzForm.getFillCycle())
                .set(BzForm::getDateType, bzForm.getDateType())
                .set(BzForm::getYear, bzForm.getYear())
                .set(BzForm::getQuarter, bzForm.getQuarter())
                .set(BzForm::getTypeId, bzForm.getTypeId());
        if (bzForm.getLeadingDepartmentId() != null) {
            updateWrapper.set(BzForm::getLeadingDepartmentId, bzForm.getLeadingDepartmentId());
        }
        if (bzForm.getLeadingDepartment() != null) {
            updateWrapper.set(BzForm::getLeadingDepartment, bzForm.getLeadingDepartment());
        }
        if (bzForm.getResponsibleDeptId() != null) {
            updateWrapper.set(BzForm::getResponsibleDeptId, bzForm.getResponsibleDeptId());
        }
        if (bzForm.getResponsibleDept() != null) {
            updateWrapper.set(BzForm::getResponsibleDept, bzForm.getResponsibleDept());
        }
        return update(updateWrapper);
    }

    @Override
    public boolean updateCheckById(Long taskId, Integer addStatus, Integer removeStatus) {
        BzForm bzForm = getById(taskId);
        String checkStatus = bzForm.getCheckStatus();

        //拼接字符串，使用逗号分割
        if (addStatus != null) {
            checkStatus = util.joinString(checkStatus, addStatus.toString());
        }
        if (removeStatus != null) {
            checkStatus = util.removeString(checkStatus, removeStatus.toString());
        }

        LambdaUpdateWrapper<BzForm> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BzForm::getId, bzForm.getId());
        updateWrapper.set(BzForm::getCheckStatus, checkStatus);

        return update(updateWrapper);
    }

    @Override
    public void updateCheckProcess(Long id, String processInstanceId, String processInstanceReviewIds) {
        LambdaUpdateWrapper<BzForm> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BzForm::getId, id)
                .set(BzForm::getProcessInstanceId, processInstanceId)
                .set(BzForm::getProcessInstanceReviewIds, processInstanceReviewIds);

        update(updateWrapper);
    }

    @Override
    public List<Map<String, Object>> countEffectiveGear() {
        return bzFormMapper.countEffectiveGear();
    }

    @Override
    public List<EffectiveGearCount> countGearCollect() {
        return bzFormMapper.countGearCollect();
    }

    @Override
    public List<EffectiveGearCount> countGearCollectTargetByDate(LocalDateTime startTime, LocalDateTime endTime) {
        return bzFormMapper.countGearCollectTargetByDate(startTime, endTime);
    }

    @Override
    public List<EffectiveGearCount> countGearCollectByDate(LocalDateTime startTime, LocalDateTime endTime) {
        return bzFormMapper.countGearCollectByDate(startTime, endTime);
    }

    @Override
    public List<BzFromTargetNameCount> selectByTimeAndGear(LocalDateTime startTime, LocalDateTime endTime, Integer gear, Integer typeId) {
        return bzFormMapper.selectByTimeAndGear(startTime, endTime, gear, typeId);
    }

    @Override
    public IPage<BzFormTarget> selectByTypeAndGear(int pageNum, int pageSize, LocalDateTime startTime, LocalDateTime endTime, Integer gear, Integer typeId) {
        Page<BzFormTarget> page = new Page<>(pageNum, pageSize);
        return bzFormMapper.selectByTypeAndGear(page, startTime, endTime, gear, typeId);
    }
}