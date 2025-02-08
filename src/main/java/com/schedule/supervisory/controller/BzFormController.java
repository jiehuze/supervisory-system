package com.schedule.supervisory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.schedule.common.BaseResponse;
import com.schedule.supervisory.entity.BzForm;
import com.schedule.supervisory.service.IBzFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bzform")
public class BzFormController {

    @Autowired
    private IBzFormService bzFormService;

    @GetMapping("/list")
    public BaseResponse list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                             @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<BzForm> bzFormPage = bzFormService.page(new Page<>(pageNum, pageSize));

        return new BaseResponse(HttpStatus.OK.value(), "success", bzFormPage, Integer.toString(0));
    }

    // 插入新记录
    @PostMapping("/add")
    public boolean addBzForm(@RequestBody BzForm bzForm) {
        return bzFormService.addBzForm(bzForm);
    }
}