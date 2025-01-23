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
public class OrderWaitExportTemplateExcel {

    @ExcelProperty(value = "专卖局", index = 0)
    private String organName;
    @ExcelProperty(value = "市场单元", index = 1)
    private String cellName;
    @ExcelProperty(value = "排队日期", index = 2)
    private String queueTime;
    @ExcelProperty(value = "排队号", index = 3)
    private String queueNum;
    @ExcelProperty(value = "排队状态", index = 4)
    private String statusCN;
    @ExcelProperty(value = "申请人姓名", index = 5)
    private String customerName;
    @ExcelProperty(value = "申请企业名称字号", index = 6)
    private String busiName;
    //    @ExcelProperty(value = "统一社会信用代码", index = 3)
//    private String busiId;
    @ExcelProperty(value = "申请人手机号", index = 7)
    private String customerTel;
    //    @ExcelProperty(value = "身份证号", index = 6)
//    private String custIdentity;
    @ExcelProperty(value = "经营地址", index = 8)
    private String addr;
    @ExcelProperty(value = "来源", index = 9)
    private String originCN;
    @ExcelProperty(value = "创建时间", index = 10)
    private String createTime;
}
