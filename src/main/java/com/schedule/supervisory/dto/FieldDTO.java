package com.schedule.supervisory.dto;

import com.schedule.supervisory.entity.Field;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FieldDTO implements Serializable {
    private Field field;
    private List<FieldDTO> children;
}
