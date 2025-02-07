package com.schedule.supervisory.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class TaskFileldCount implements Serializable {
    private List<Map<String, Object>> totals;
    private List<Map<String, Object>> complete_totals;
}
