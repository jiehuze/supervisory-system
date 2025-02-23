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
    @Value("${duban.oauth2}")
    private String authUrl;

    @Value("${duban.users}")
    private String usersUrl;
}
