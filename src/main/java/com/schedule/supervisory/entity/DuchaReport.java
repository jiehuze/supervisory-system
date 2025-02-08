package com.schedule.supervisory.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ducha_report")
public class DuchaReport {
    @TableId
    private Long id; // 自增主键

    private String tasks; // 任务字段，现称为tasks，类型为字符串
    private String reportName; // 报告名字
    private String submitter; // 报送人
    private String submitterId; // 报送人ID
    private Boolean isSubmitted; // 是否报送，默认为未报送(false)
    private Boolean isDeleted; // 是否删除，默认为未删除(false)
    private String leadingOfficial; // 新字段名：报送领导（现称为leading_official）
    private String leadingOfficialId; // 新字段名：领导ID（现称为leading_official_id），使用字符串类型
    private String reportFile; // 报告文件路径，假设以字符串形式存储
    private java.sql.Timestamp createdAt; // 创建时间，默认为当前时间
    private Integer period; // 期数字段
    private Integer taskCount; // 任务总数
    private Integer inProgressCount; // 推进中的任务数
    private Integer overdueCount; // 超期的任务数
    private Integer issueCount; // 存在问题的任务
    private Integer completeOnTimeCount; // 按时完结
    private Integer completeCount; //完结任务
}