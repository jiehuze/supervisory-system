package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.entity.BzType;
import com.schedule.supervisory.entity.Field;

import java.util.List;

public interface IFieldService extends IService<Field> {
    List<Field> getFields();

    Field getFieldByName(String name);

    boolean deleteById(Long id);

    boolean recover(Long id);
}
