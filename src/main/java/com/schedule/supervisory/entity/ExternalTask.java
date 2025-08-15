package com.schedule.supervisory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data // Lombok annotation to generate getters and setters
@TableName("external_task") // 指定对应的数据库表名
public class ExternalTask {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String content;

    private String deadline;

    private String updateBy;

    private String leadingDepartment;

    private LocalDateTime createTime;
}
