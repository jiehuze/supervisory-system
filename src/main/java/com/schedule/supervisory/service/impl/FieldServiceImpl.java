package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.FieldMapper;
import com.schedule.supervisory.entity.Field;
import com.schedule.supervisory.service.IFieldService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FieldServiceImpl extends ServiceImpl<FieldMapper, Field> implements IFieldService {

}