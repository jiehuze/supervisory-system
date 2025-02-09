package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.BzFormMapper;
import com.schedule.supervisory.entity.BzForm;
import com.schedule.supervisory.entity.Task;
import com.schedule.supervisory.service.IBzFormService;
import org.springframework.stereotype.Service;

@Service
public class BzFormServiceImpl extends ServiceImpl<BzFormMapper, BzForm> implements IBzFormService {
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
    public IPage<BzForm> getBzFormByConditions(BzForm queryBzform, int pageNum, int pageSize) {
        //todo 读取用户的权限，根据权限判断要读取什么样的数据
        //权限有如下几种：1：承办人，只需要查看本单位下的数据；2：交办人：只需要看本人下的数据；3：承办领导：本部门及下属部门  4：领导：可以看到所有
        //1）交办人只读取自己创建的任务；2）承办人：只看自己负责的任务；3）交办领导：只看自己负责的部门；4）承包领导：只看自己负责的部门
        //所以看获取的人员部门数组；如果数组为空：判断创建人或者责任人；如果不为空，需要查询包含部门的数据
        // 创建分页对象
        Page<BzForm> page = new Page<>(pageNum, pageSize);

        // 构建查询条件
        LambdaQueryWrapper<BzForm> queryWrapper = new LambdaQueryWrapper<>();
        if (queryBzform.getName() != null && !queryBzform.getName().isEmpty()) {
            queryWrapper.like(BzForm::getName, queryBzform.getName());
        }

        if (queryBzform.getTypeId() != null) {
            queryWrapper.eq(BzForm::getTypeId, queryBzform.getTypeId());
        }

        if (queryBzform.getPredictedGear() != null) {
            queryWrapper.eq(BzForm::getPredictedGear, queryBzform.getPredictedGear());
        }

        queryWrapper.orderByDesc(BzForm::getId);

        return page(page, queryWrapper);
    }

    @Override
    public boolean updateBzFrom(BzForm bzForm) {
        LambdaUpdateWrapper<BzForm> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BzForm::getId, bzForm.getId())
                .set(BzForm::getActualGear, bzForm.getActualGear())
                .set(BzForm::getPredictedGear, bzForm.getPredictedGear())
                .set(BzForm::getType, bzForm.getType())
                .set(BzForm::getName, bzForm.getName())
                .set(BzForm::getFillCycle, bzForm.getFillCycle())
                .set(BzForm::getTypeId, bzForm.getTypeId());
        return update(updateWrapper);
    }
}