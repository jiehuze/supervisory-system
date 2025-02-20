package com.schedule.supervisory.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("bz_form")
public class BzForm {
    @TableId
    private Long id; // 表id
    private String name; // 名称
    private String type; // 类型
    private Integer typeId; // 类型id
    private Integer fillCycle; //通报周期
    private Integer actualGear; // 档位
    private Integer predictedGear; //预估档位
    private String creator; // 创建人
    private String creatorId; // 创建人id
    private Integer dateType; // 新增的日期类型字段
    private Integer year; // 年份
    private Integer quarter; // 季度
    private String leadingDepartment; // 牵头单位 'leading_department'
    private String leadingDepartmentId; // 牵头单位ID 'leading_department_id'
    private String responsibleDept; // 责任部门
    private String responsibleDeptId; // 责任部门id
    private java.sql.Timestamp createdAt; // 创建时间
    private java.sql.Timestamp updatedAt; // 更新时间
}