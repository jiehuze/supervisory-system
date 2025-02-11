package com.schedule.supervisory.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class EffectiveGearCount implements Serializable {
    private Integer effectiveGear;
    private Long countEffectiveGear;
}
