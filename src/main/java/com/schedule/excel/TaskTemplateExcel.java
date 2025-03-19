package com.schedule.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.alibaba.excel.enums.poi.VerticalAlignmentEnum;
import lombok.Data;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.time.LocalDate;

@Data
@ContentRowHeight(40)
@HeadRowHeight(40)
@ColumnWidth(40)
public class TaskTemplateExcel {

    @ExcelProperty(value = "序号", index = 0)
    @ContentStyle(wrapped = BooleanEnum.TRUE,
            horizontalAlignment = HorizontalAlignmentEnum.CENTER,  // 水平居中
            verticalAlignment = VerticalAlignmentEnum.CENTER       // 垂直居中
    ) // 自动换行
    private Integer num; // 任务来源 'source'
    @ExcelProperty(value = "任务来源", index = 1)
    @ContentStyle(wrapped = BooleanEnum.TRUE) // 自动换行
    private String source; // 任务来源 'source'
    @ExcelProperty(value = "任务内容", index = 2)
    @ContentStyle(wrapped = BooleanEnum.TRUE) // 自动换行
    private String content; // 任务内容 'content'
    @ExcelProperty(value = "牵头领导", index = 3)
    private String leadingOfficial; // 牵头区领导 'leading_official'
    @ExcelProperty(value = "牵头单位", index = 4)
    @ContentStyle(wrapped = BooleanEnum.TRUE) // 自动换行
    private String leadingDepartment; // 牵头单位 'leading_department'
    @ExcelProperty(value = "协办单位", index = 5)
    @ContentStyle(wrapped = BooleanEnum.TRUE) // 自动换行
    private String coOrganizer; // 牵头单位 'leading_department'
    @ExcelProperty(value = "完成时限", index = 6)
    @ContentStyle(wrapped = BooleanEnum.TRUE,
            horizontalAlignment = HorizontalAlignmentEnum.CENTER,  // 水平居中
            verticalAlignment = VerticalAlignmentEnum.CENTER       // 垂直居中
    ) // 自动换行
    private LocalDate deadline; // 完成时间 'deadline'
    @ExcelProperty(value = "具体进展", index = 7)
    @ContentStyle(wrapped = BooleanEnum.TRUE) // 自动换行
    private String progress; // 具体进展 'progress'
    @ExcelProperty(value = "存在的困难或问题", index = 8)
    @ContentStyle(wrapped = BooleanEnum.TRUE) // 自动换行
    private String issuesAndChallenges; // 存在的问题或者困难 'issues_and_challenges'
    @ExcelProperty(value = "完成情况", index = 9)
    @ContentStyle(wrapped = BooleanEnum.TRUE) // 自动换行
    private String status; // 任务状态 'status'
}
