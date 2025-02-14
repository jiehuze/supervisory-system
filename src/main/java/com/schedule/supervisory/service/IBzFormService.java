package com.schedule.supervisory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schedule.supervisory.dto.BzFromTargetNameCount;
import com.schedule.supervisory.dto.EffectiveGearCount;
import com.schedule.supervisory.entity.BzForm;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IBzFormService extends IService<BzForm> {
    boolean addBzForm(BzForm bzForm);

    Long insertBzForm(BzForm bzForm);

    IPage<BzForm> getBzFormByConditions(BzForm queryBzform, int pageNum, int pageSize);

    boolean updateBzFrom(BzForm bzForm);

    List<Map<String, Object>> countEffectiveGear();

    List<EffectiveGearCount> countGearCollect();

    List<EffectiveGearCount> countGearCollectByQuarter(LocalDateTime startTime, LocalDateTime endTime);

    List<BzFromTargetNameCount> selectByTimeAndGear(LocalDateTime startTime, LocalDateTime endTime, Integer gear);
}