package com.schedule.supervisory.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data // Lombok annotation to generate getters and setters
@TableName("field") // 指定对应的数据库表名
public class Field {
    @TableId // 主键标识
    private Long id; // 对应数据库列 'id'
    private String name; // 对应数据库列 'name'
    private String description; // 对应数据库列 'description'
    private boolean delete;
    private Timestamp createdAt; // 对应数据库列 'created_at'
    private Timestamp updatedAt; // 对应数据库列 'updated_at'
}