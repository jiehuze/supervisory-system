package com.schedule.supervisory.dto;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class ParameterDTO {
    @Value("${duban.permission}")
    private String permissionUrl;
    @Value("${duban.upload}")
    private String uploadUrl;
    @Value("${duban.message}")
    private String messageUrl;
    @Value("${duban.message.phone}")
    private String phoneMessageUrl;
    @Value("${duban.message.pc}")
    private String pcMessageUrl;
    @Value("${duban.message.form.phone}")
    private String phoneFormMessageUrl;
    @Value("${duban.message.form.pc}")
    private String pcFormMessageUrl;
    @Value("${duban.message.issue.phone}")
    private String phoneIssueMessageUrl;
    @Value("${duban.message.issue.pc}")
    private String pcIssueMessageUrl;
    @Value("${spring.profiles.active}")
    private String serviceEnv;

    @Value("${duban.oauth2}")
    private String authUrl;

    @Value("${duban.users}")
    private String usersUrl;

    @Value("${duban.check.start}")
    private String checkStart;
    @Value("${duban.check.detail}")
    private String checkDetail;
    @Value("${duban.check.format}")
    private String checkFormat;
    @Value("${duban.check.complete}")
    private String checkComplete;
    @Value("${duban.person.get.dept}")
    private String personWithDepts;
}
