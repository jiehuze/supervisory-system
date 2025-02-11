package com.schedule.supervisory.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CountDTO implements Serializable {
    private int count;
    private String percentage;

    public CountDTO(int totalCount, String percentage) {
        this.count = totalCount;
        this.percentage = percentage;
    }
}
