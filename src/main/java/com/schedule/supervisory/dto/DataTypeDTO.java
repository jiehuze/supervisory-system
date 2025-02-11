package com.schedule.supervisory.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class DataTypeDTO implements Serializable {
    private int typeId;
    private int total;
    private Map<Integer, CountDTO> countDTOMap;
}
