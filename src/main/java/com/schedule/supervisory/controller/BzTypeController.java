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

    /**
     * 批量更新接口，当调整顺序时可以调用，修改typeId为从1开始的顺序
     *
     * @param bzTypeList 待保存的BzType列表。
     * @return BaseResponse 包含操作结果的信息。
     */
    @PostMapping("/order")
    public BaseResponse order(@RequestBody List<BzType> bzTypeList) {
        Integer maxTypeId = bzTypeService.getMaxTypeId(bzTypeList.get(0).getType());
        for (BzType bzType : bzTypeList) {
            if (bzType.getId() == null) {
                bzType.setTypeId(++maxTypeId);
            }
        }
//        bzTypeService.updateBatchById(bzTypeList);
        bzTypeService.saveOrUpdateBatch(bzTypeList);
        return new BaseResponse(HttpStatus.OK.value(), "success", 0, Integer.toString(0));
    }

    @DeleteMapping("/{id}")
    public BaseResponse delete(@PathVariable Long id) {
//        boolean result = bzTypeService.removeById(id);
        boolean result = bzTypeService.deleteById(id);
        return new BaseResponse(HttpStatus.OK.value(), "success", result, Integer.toString(0));
    }
}