package com.schedule.supervisory.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BzFromTargetNameCount implements Serializable {
    private String name;
    private Long count;
}
