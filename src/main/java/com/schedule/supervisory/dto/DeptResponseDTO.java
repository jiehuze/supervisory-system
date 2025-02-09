package com.schedule.supervisory.dto;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DeptResponseDTO implements Serializable {
    private int code;
    private String msg;
    private Object data;
}
