package com.schedule.supervisory.controller;

import com.schedule.common.BaseResponse;
import com.schedule.supervisory.entity.Field;
import com.schedule.supervisory.service.IFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/fields")
public class FieldController {

    @Autowired
    private IFieldService fieldService;

    @GetMapping("/all")
    public BaseResponse getAllFields() {
        List<Field> list = fieldService.list();// 调用IService的list方法获取所有记录
        return new BaseResponse(HttpStatus.OK.value(), "success", list, Integer.toString(0));
    }
}