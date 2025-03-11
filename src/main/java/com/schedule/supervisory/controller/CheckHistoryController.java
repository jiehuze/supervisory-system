package com.schedule.supervisory.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.schedule.supervisory.entity.CheckHistory;
import com.schedule.supervisory.service.ICheckHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/check-history")
public class CheckHistoryController {

    @Autowired
    private ICheckHistoryService checkHistoryService;

    // 添加新的审核记录
    @PostMapping("/add")
    public boolean addCheckHistory(@RequestBody CheckHistory checkHistory) {
        return checkHistoryService.save(checkHistory);
    }

    // 通过ID查询审核记录
    @GetMapping("/getById/{id}")
    public CheckHistory getCheckHistoryById(@PathVariable Long id) {
        return checkHistoryService.getById(id);
    }

    // 通过check_id查询审核记录
    @GetMapping("/getByCheckId/{checkId}")
    public CheckHistory getCheckHistoryByCheckId(@PathVariable Integer checkId) {
        QueryWrapper<CheckHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("check_id", checkId);
        return checkHistoryService.getOne(queryWrapper);
    }
}