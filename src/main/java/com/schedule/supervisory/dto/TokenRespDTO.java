package com.schedule.supervisory.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TokenRespDTO implements Serializable {
    private String access_token;
    private String token_type;
    private long expires_in;
}
