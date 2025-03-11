package com.schedule.supervisory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("progress_report")
public class ProgressReport {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long taskId;

    private Integer stageNodeId;

    private String progress;

    private String issuesAndChallenges;

    private Boolean requiresCoordination = false;

    private String nextSteps;

    private String handler;

    private String phone;

    private LocalDateTime createdAt;

    /**
     * 1: 已撤回
     * 2：撤回失败
     * 3：完成 （包括审核成功状态）
     * 4：审核包括审核中
     * 5: 审核失败
     */
    private Integer status;
    private String revokeDesc; // 新增的撤回描述字段

    private String tbFileUrl; //通报文件
    private Long checkId;
    private String submitId; //提交人
}