package com.schedule.supervisory.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProcessNodeDTO {
    private String id;
    private List<UserVo> userVoList;
    private String placeholder;
    private Integer status;
    private String name;
    private Integer type;
    private Boolean selectUser;
    private Boolean multiple;
    //    private List<Node> children;
    private List<Object> branch; // 如果分支可以是任意类型，则使用Object或更具体的类型

    // Getters and Setters

    @Data
    public static class UserVo {
        private String id;
        private String name;
        private String showTime;
        private Object avatar; // 可能为null，因此使用Object或者特定类型
        private String approveDesc;
        private String operType;
        private Integer status;

        // Getters and Setters
    }
}
