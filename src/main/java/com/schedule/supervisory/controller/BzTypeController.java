package com.schedule.supervisory.controller;

import com.schedule.common.BaseResponse;
import com.schedule.supervisory.entity.BzType;
import com.schedule.supervisory.service.IBzTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bztype")
public class BzTypeController {

    @Autowired
    private IBzTypeService bzTypeService;

    @PostMapping
    public BaseResponse create(@RequestBody BzType bzType) {
        boolean result = bzTypeService.save(bzType);
        return new BaseResponse(HttpStatus.OK.value(), "success", result, Integer.toString(0));
    }

    @GetMapping("/{id}")
    public BaseResponse read(@PathVariable Integer id) {
        BzType list = bzTypeService.getById(id);
        return new BaseResponse(HttpStatus.OK.value(), "success", list, Integer.toString(0));

    }

    @GetMapping
    public BaseResponse read(@ModelAttribute BzType bzType) {
        List<BzType> bzTypeByContains = bzTypeService.getBzTypeByContains(bzType);
        return new BaseResponse(HttpStatus.OK.value(), "success", bzTypeByContains, Integer.toString(0));

    }

    @PutMapping
    public BaseResponse update(@RequestBody BzType bzType) {
        boolean result = bzTypeService.updateById(bzType);
        return new BaseResponse(HttpStatus.OK.value(), "success", result, Integer.toString(0));
    }

    @DeleteMapping("/{id}")
    public BaseResponse delete(@PathVariable Integer id) {
        boolean result = bzTypeService.removeById(id);
        return new BaseResponse(HttpStatus.OK.value(), "success", result, Integer.toString(0));
    }
}