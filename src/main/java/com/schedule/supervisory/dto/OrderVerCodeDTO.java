package com.schedule.supervisory.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("order_vercode")
public class OrderVerCodeDTO implements Serializable {
    private Long id;
    private String phoneNumber;
    private String code;
    private String expired;
    private Date createTime;

}
