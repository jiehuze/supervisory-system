package com.schedule.supervisory.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TaskFields implements Serializable {
    private Long id;
    private String source; // 任务来源 'source'
    private String content; // 任务内容 'content'
    private String fieldIds; // 所属领域，第一级ID列表
    private String fieldSecondIds; // 第二级领域 ID 列表
    private String fieldThirdIds; // 第三级领域 ID 列表
    private String fieldNames; // 领域名称列表
}
