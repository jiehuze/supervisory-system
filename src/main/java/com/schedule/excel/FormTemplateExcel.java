package com.schedule.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class FormTemplateExcel {

    @ExcelProperty(value = "清单", index = 0)
    private String type;
    @ExcelProperty(value = "清单预估指标", index = 1)
    private String predictedGear;
    @ExcelProperty(value = "指标名称", index = 2)
    private String name;
    @ExcelProperty(value = "责任单位", index = 3)
    private String dept;
    @ExcelProperty(value = "工作进展", index = 4)
    private String workProgress;
    @ExcelProperty(value = "存在问题", index = 5)
    private String issues;
}
