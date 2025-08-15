package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.ExternalTaskMapper;
import com.schedule.supervisory.entity.ExternalTask;
import com.schedule.supervisory.service.IExternalTaskService;
import org.springframework.stereotype.Service;

@Service
public class ExternalTaskServiceImpl extends ServiceImpl<ExternalTaskMapper, ExternalTask> implements IExternalTaskService {

    @Override
    public IPage<ExternalTask> getExternalTasks(int pageNum, int pageSize) {
        Page<ExternalTask> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectPage(page, null); // 查询所有数据的分页
    }

    @Override
    public Long addExternalTask(ExternalTask externalTask) {
        boolean result = save(externalTask);
        if (result) {
            return externalTask.getId();
        } else {
            return null;
        }
    }
}