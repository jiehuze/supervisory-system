package com.schedule.supervisory.controller;

import com.schedule.common.BaseResponse;
import com.schedule.supervisory.entity.Check;
import com.schedule.supervisory.service.ICheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/check")
public class CheckController {

    @Autowired
    private ICheckService checkService;

    @PostMapping("/add")
    public BaseResponse add(@RequestBody Check check) {
        boolean save = checkService.save(check);
        return new BaseResponse(HttpStatus.OK.value(), "success", save, Integer.toString(0));
    }


    /**
     * 审核填报相关数据
     */
    @PutMapping("/status")
    public BaseResponse update(@ModelAttribute Check check) {
        boolean checkStatus = checkService.checkStatus(check);
        return new BaseResponse(HttpStatus.OK.value(), "success", checkStatus, Integer.toString(0));
    }

    @GetMapping("/get")
    public BaseResponse getByTaskIdAndStageId(@ModelAttribute Check check) {
        Check checkInfo = checkService.getByOnlyId(check);
        return new BaseResponse(HttpStatus.OK.value(), "success", checkInfo, Integer.toString(0));
    }
}