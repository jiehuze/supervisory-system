package com.schedule.supervisory.controller;

import com.schedule.common.BaseResponse;
import com.schedule.supervisory.entity.BzType;
import com.schedule.supervisory.entity.Field;
import com.schedule.supervisory.service.IFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fields")
public class FieldController {

    @Autowired
    private IFieldService fieldService;

    @PostMapping("/add")
    public BaseResponse create(@RequestBody Field field) {
        boolean result = true;
        Field fieldByName = fieldService.getFieldByName(field.getName());
        if (fieldByName == null) {
            result = fieldService.save(field);
        } else {
            if (fieldByName.isDelete() == true) {
                result = fieldService.recover(fieldByName.getId());
            }
        }
        return new BaseResponse(HttpStatus.OK.value(), "success", result, Integer.toString(0));
    }

    @GetMapping("/all")
    public BaseResponse getAllFields() {
//        List<Field> list = fieldService.list();// 调用IService的list方法获取所有记录
        List<Field> list = fieldService.getFields();
        return new BaseResponse(HttpStatus.OK.value(), "success", list, Integer.toString(0));
    }

    @DeleteMapping("/{id}")
    public BaseResponse delete(@PathVariable Long id) {
        boolean result = fieldService.deleteById(id);
        return new BaseResponse(HttpStatus.OK.value(), "success", result, Integer.toString(0));
    }
}