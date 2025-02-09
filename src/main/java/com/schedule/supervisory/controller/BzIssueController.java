package com.schedule.supervisory.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.schedule.common.BaseResponse;
import com.schedule.supervisory.dto.BzIssueDTO;
import com.schedule.supervisory.dto.BzIssueDTO;
import com.schedule.supervisory.entity.BzIssue;
import com.schedule.supervisory.entity.BzIssueTarget;
import com.schedule.supervisory.service.IBzIssueService;
import com.schedule.supervisory.service.IBzIssueTargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bzIssue")
public class BzIssueController {

    @Autowired
    private IBzIssueService bzIssueService;

    @Autowired
    private IBzIssueTargetService bzIssueTargetService;

    @GetMapping("/search")
    public BaseResponse list(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                             @RequestHeader(value = "tenant-id", required = false) String tenantId,
                             @ModelAttribute BzIssue bzIssue,
                             @RequestParam(value = "current", defaultValue = "1") Integer pageNum,
                             @RequestParam(value = "size", defaultValue = "10") Integer pageSize) {
//        Page<BzIssue> bzIssuePage = bzIssueService.page(new Page<>(pageNum, pageSize));
        IPage<BzIssue> bzIssueByConditions = bzIssueService.getBzIssueByConditions(bzIssue, pageNum, pageSize);

        return new BaseResponse(HttpStatus.OK.value(), "success", bzIssueByConditions, Integer.toString(0));
    }
    @GetMapping("/detail/{id}")
    public BaseResponse detail(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                               @RequestHeader(value = "tenant-id", required = false) String tenantId,
                               @PathVariable Long id) {
        BzIssueDTO bzIssueDTO = new BzIssueDTO();
        bzIssueDTO.setBzIssue(bzIssueService.getById(id));
        bzIssueDTO.setBzIssueTargetList(bzIssueTargetService.getByIssueId(id));

        return new BaseResponse(HttpStatus.OK.value(), "success", bzIssueDTO, Integer.toString(0));
    }
    @PostMapping("/add")
    public BaseResponse saveOrUpdateTasks(@RequestBody BzIssueDTO bzFromDTO) {
        BzIssue bzIssue = bzFromDTO.getBzIssue();
        Long id = bzIssueService.insertBzIssue(bzIssue);
        if (id == null) {
            return new BaseResponse(HttpStatus.NO_CONTENT.value(), "failed", id, Integer.toString(0));
        }
        for (BzIssueTarget bzIssueTarget : bzFromDTO.getBzIssueTargetList()) {
            bzIssueTarget.setBzIssueId(id);
        }
        if (bzFromDTO.getBzIssueTargetList().size() != 0) {
            bzIssueTargetService.saveBatch(bzFromDTO.getBzIssueTargetList());
        }

        return new BaseResponse(HttpStatus.OK.value(), "success", 0, Integer.toString(0));
    }

    @PutMapping("/update")
    public BaseResponse updateBzIssue(@RequestBody BzIssue bzIssue) {
        boolean upate = bzIssueService.updateBzFrom(bzIssue);
        return new BaseResponse(HttpStatus.OK.value(), "success", upate, Integer.toString(0));
    }
}