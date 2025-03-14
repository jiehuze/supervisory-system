package com.schedule.supervisory.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CheckUserDTO implements Serializable {
    private String type;
    private String id;
    private String name;
    private String avatar;
}
