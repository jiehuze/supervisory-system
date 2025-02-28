package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.FieldMapper;
import com.schedule.supervisory.entity.BzType;
import com.schedule.supervisory.entity.Field;
import com.schedule.supervisory.service.IFieldService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FieldServiceImpl extends ServiceImpl<FieldMapper, Field> implements IFieldService {

    @Override
    public List<Field> getFields(Boolean delete) {
        LambdaQueryWrapper<Field> queryWrapper = new LambdaQueryWrapper<>();
        if (delete != null) {
            queryWrapper.eq(Field::isDelete, delete); // 查询没有删除
        }
        queryWrapper.orderByAsc(Field::getId);
        return this.list(queryWrapper);
    }

    @Override
    public Field getFieldByName(String name) {
        LambdaQueryWrapper<Field> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Field::getName, name); // 查询没有删除
        return getOne(queryWrapper);
    }

    @Override
    public boolean deleteById(Long id) {
        LambdaUpdateWrapper<Field> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Field::getId, id);
        updateWrapper.set(Field::isDelete, true);
        return update(updateWrapper);
    }

    /**
     * 重新启用
     *
     * @param id
     * @return
     */
    @Override
    public boolean recover(Long id) {
        LambdaUpdateWrapper<Field> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Field::getId, id);
        updateWrapper.set(Field::isDelete, false);
        return update(updateWrapper);
    }
}