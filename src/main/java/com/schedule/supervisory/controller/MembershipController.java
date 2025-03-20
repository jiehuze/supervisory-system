package com.schedule.supervisory.controller;

import com.schedule.common.BaseResponse;
import com.schedule.supervisory.entity.Membership;
import com.schedule.supervisory.service.IMembershipService;
import com.schedule.utils.util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/membership")
public class MembershipController {

    @Autowired
    private IMembershipService membershipService;

    @PostMapping("/add")
    public BaseResponse addMembership(@RequestBody Membership membership) {
        boolean add = membershipService.addMembership(membership);
        return new BaseResponse(HttpStatus.OK.value(), "success", add, Integer.toString(0));
    }

    @PutMapping("/update")
    public BaseResponse updateMembership(@RequestBody Membership membership) {
        boolean update = membershipService.updateMembership(membership);
        return new BaseResponse(HttpStatus.OK.value(), "success", update, Integer.toString(0));
    }

    @PutMapping("/save")
    public BaseResponse saveMembership(@RequestBody Membership membership) {
        boolean save = true;
        Membership mp = membershipService.getByLeadingDepartmentId(membership.getLeadingDepartmentId());
        if (mp != null) {
            membership.setId(mp.getId());
        }
        System.out.println("==========save: "+ membership.toString());

        save = membershipService.saveOrUpdate(membership);
        return new BaseResponse(HttpStatus.OK.value(), "success", save, Integer.toString(0));
    }

    @GetMapping("/get/{leadingDepartmentId}")
    public BaseResponse getByLeadingDepartmentId(@PathVariable String leadingDepartmentId) {
        Membership membership = membershipService.getByLeadingDepartmentId(leadingDepartmentId);
        if (membership != null) {
            String phone = membership.getPhone();
            if (phone != null) {
                membership.setPhone(util.maskPhoneNumber(phone));
            }
        }

        return new BaseResponse(HttpStatus.OK.value(), "success", membership, Integer.toString(0));
    }
}