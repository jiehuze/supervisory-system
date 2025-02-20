package com.schedule.supervisory.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data // Lombok annotation to generate getters and setters
@TableName("bz_instruction") // 指定对应的数据库表名
public class BzInstruction {
    @TableId // 主键标识
    private Integer id; // 对应数据库列 'id'
    private Integer bzFormId; // 对应数据库列 'task_id'
    private Integer bzIssueId; // 对应数据库列 'task_id'
    private Integer bzTargetId; // 对应数据库列 'task_id'
    private String reviewerId; // 新增字段：对应数据库列 'reviewer_id'
    private String reviewer; // 对应数据库列 'reviewer'
    private String content; // 对应数据库列 'content'
    private Timestamp reviewTime; // 对应数据库列 'review_time'
}
