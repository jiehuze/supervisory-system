package com.schedule.supervisory.controller;

import com.schedule.common.BaseResponse;
import com.schedule.supervisory.dto.FieldDTO;
import com.schedule.supervisory.entity.Field;
import com.schedule.supervisory.service.IFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            result = fieldService.insertField(field) > 0 ? true : false;
        } else {
            if (fieldByName.isDelete() == true) {
                result = fieldService.recover(fieldByName.getId());
            }
        }
        return new BaseResponse(HttpStatus.OK.value(), "success", result, Integer.toString(0));
    }

    @GetMapping("/all")
    public BaseResponse getAllFields(@RequestParam(required = false) Boolean deleteField) {
        List<Field> list = fieldService.getFields(deleteField);
        return new BaseResponse(HttpStatus.OK.value(), "success", list, Integer.toString(0));
    }

    @GetMapping("/tree")
    public BaseResponse getTreeFields(@RequestParam(required = false) Boolean deleteField) {
        ArrayList<FieldDTO> fieldDTOTree = new ArrayList<>();

        List<Field> firstFields = fieldService.getFieldsByParentId(deleteField, (long) 0);
        for (Field first : firstFields) {

            ArrayList<FieldDTO> secondFieldDTOS = new ArrayList<>();
            FieldDTO firstFieldDTO = new FieldDTO();
            firstFieldDTO.setField(first);

            List<Field> secondFields = fieldService.getFieldsByParentId(deleteField, first.getId());
            for (Field second : secondFields) {
                FieldDTO secondFieldDTO = new FieldDTO();
                secondFieldDTO.setField(second);

                ArrayList<FieldDTO> thirdFieldDTOS = new ArrayList<>();
                List<Field> thirdFields = fieldService.getFieldsByParentId(deleteField, second.getId());

                for (Field third : thirdFields) {
                    FieldDTO thirdFieldDTO = new FieldDTO();
                    thirdFieldDTO.setField(third);
                    thirdFieldDTOS.add(thirdFieldDTO);
                }

                secondFieldDTO.setChildren(thirdFieldDTOS);
                secondFieldDTOS.add(secondFieldDTO);
            }

            firstFieldDTO.setChildren(secondFieldDTOS);
            fieldDTOTree.add(firstFieldDTO);
        }
        return new BaseResponse(HttpStatus.OK.value(), "success", fieldDTOTree, Integer.toString(0));
    }

    @GetMapping("/treeData")
    public BaseResponse getTreeDataFields(@RequestParam(required = false) Boolean deleteField) {
        List<Map<String, Object>> treeData = fieldService.getTreeData(deleteField);
        return new BaseResponse(HttpStatus.OK.value(), "success", treeData, Integer.toString(0));
    }

    @DeleteMapping("/{id}")
    public BaseResponse delete(@PathVariable Long id) {
        boolean result = fieldService.deleteById(id);
        return new BaseResponse(HttpStatus.OK.value(), "success", result, Integer.toString(0));
    }
}