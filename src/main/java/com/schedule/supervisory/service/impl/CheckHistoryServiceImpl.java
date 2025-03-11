package com.schedule.supervisory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schedule.supervisory.dao.mapper.CheckHistoryMapper;
import com.schedule.supervisory.entity.CheckHistory;
import com.schedule.supervisory.service.ICheckHistoryService;
import org.springframework.stereotype.Service;

@Service
public class CheckHistoryServiceImpl extends ServiceImpl<CheckHistoryMapper, CheckHistory> implements ICheckHistoryService {
}