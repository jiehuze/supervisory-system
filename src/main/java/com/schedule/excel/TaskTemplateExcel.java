package com.schedule.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.time.LocalDate;

@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class TaskTemplateExcel {

    @ExcelProperty(value = "任务来源", index = 0)
    private String source; // 任务来源 'source'
    @ExcelProperty(value = "任务内容", index = 1)
    private String content; // 任务内容 'content'
    @ExcelProperty(value = "牵头领导", index = 2)
    private String leadingOfficial; // 牵头区领导 'leading_official'
    @ExcelProperty(value = "牵头单位", index = 3)
    private String leadingDepartment; // 牵头单位 'leading_department'
    @ExcelProperty(value = "完成时限", index = 4)
    private LocalDate deadline; // 完成时间 'deadline'
    @ExcelProperty(value = "完成情况", index = 5)
    private Integer status; // 任务状态 'status'
    @ExcelProperty(value = "具体进展", index = 6)
    private String progress; // 具体进展 'progress'
    @ExcelProperty(value = "存在的困难或问题", index = 7)
    private String issuesAndChallenges; // 存在的问题或者困难 'issues_and_challenges'
}
