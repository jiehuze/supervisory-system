package com.schedule.supervisory.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.schedule.common.BaseResponse;
import com.schedule.supervisory.dto.BzFormDTO;
import com.schedule.supervisory.entity.BzForm;
import com.schedule.supervisory.entity.BzFormTarget;
import com.schedule.supervisory.service.IBzFormService;
import com.schedule.supervisory.service.IBzFormTargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bzform")
public class BzFormController {

    @Autowired
    private IBzFormService bzFormService;

    @Autowired
    private IBzFormTargetService bzFormTargetService;

    @GetMapping("/search")
    public BaseResponse list(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                             @RequestHeader(value = "tenant-id", required = false) String tenantId,
                             @ModelAttribute BzForm bzForm,
                             @RequestParam(value = "current", defaultValue = "1") Integer pageNum,
                             @RequestParam(value = "size", defaultValue = "10") Integer pageSize) {
//        Page<BzForm> bzFormPage = bzFormService.page(new Page<>(pageNum, pageSize));
        IPage<BzForm> bzFormByConditions = bzFormService.getBzFormByConditions(bzForm, pageNum, pageSize);

        return new BaseResponse(HttpStatus.OK.value(), "success", bzFormByConditions, Integer.toString(0));
    }

    @GetMapping("/detail/{id}")
    public BaseResponse detail(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                               @RequestHeader(value = "tenant-id", required = false) String tenantId,
                               @PathVariable Long id) {
        BzFormDTO bzFormDTO = new BzFormDTO();
        bzFormDTO.setBzForm(bzFormService.getById(id));
        bzFormDTO.setBzFormTargetList(bzFormTargetService.getByFormId(id));

        return new BaseResponse(HttpStatus.OK.value(), "success", bzFormDTO, Integer.toString(0));
    }

    @PostMapping("/add")
    public BaseResponse saveOrUpdateTasks(@RequestBody BzFormDTO bzFromDTO) {
        BzForm bzForm = bzFromDTO.getBzForm();
        Long id = bzFormService.insertBzForm(bzForm);
        if (id == null) {
            return new BaseResponse(HttpStatus.NO_CONTENT.value(), "failed", id, Integer.toString(0));
        }
        for (BzFormTarget bzFormTarget : bzFromDTO.getBzFormTargetList()) {
            bzFormTarget.setBzFormId(id);
        }
        if (bzFromDTO.getBzFormTargetList().size() != 0) {
            bzFormTargetService.saveBatch(bzFromDTO.getBzFormTargetList());
        }

        return new BaseResponse(HttpStatus.OK.value(), "success", 0, Integer.toString(0));
    }

    @PutMapping("/update")
    public BaseResponse updateBzForm(@RequestBody BzForm bzForm) {
        boolean upate = bzFormService.updateBzFrom(bzForm);
        return new BaseResponse(HttpStatus.OK.value(), "success", upate, Integer.toString(0));
    }
}