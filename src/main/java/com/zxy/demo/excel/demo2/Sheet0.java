package com.zxy.demo.excel.demo2;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.text.SimpleDateFormat;

import static com.zxy.demo.excel.demo2.Utils.buildRow;
import static com.zxy.demo.excel.demo2.Utils.getShowDoublePrice;


/**
 */
public class Sheet0 {

    public static void dealSheet0(Workbook wb, Sheet sheet) {
        int startRow = 0;

        // 调整单元格宽度
        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(2, 3000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 1000);  // 这一列是空行
        sheet.setColumnWidth(5, 4000);
        sheet.setColumnWidth(6, 3000);
        sheet.setColumnWidth(7, 3000);
        sheet.setColumnWidth(8, 3000);

        // 表的第一行说明文字
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 0, 8));

        Row row = sheet.createRow(startRow);

        Cell cell = row.createCell(0);
        cell.setCellStyle(CellStyleUtils.buildStyleForTitle1(wb));
        cell.setCellValue("日报表");
        startRow ++;

        // 表头说明部分
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 0, 8));
        Row rowDesc = sheet.createRow(startRow);
        Cell cellDesc = rowDesc.createCell(0);
        rowDesc.setHeight((short) (rowDesc.getHeight() * 2));   // 占据两行
        cellDesc.setCellStyle(CellStyleUtils.buildStyleForTitleDesc(wb));
        cellDesc.setCellValue("深圳分公司跟广州分公司\n导出时间：2019-01-01 12:00:00");
        startRow ++;

        dealSheet0(wb, sheet, startRow, 0);
        dealSheet0(wb, sheet, startRow, 5); // 中间空一列，所以从5开始
    }

    /*
    这里由于字段少，而且调整字段的顺序对整体的改动较小，所以在createCell基本是写死需要在哪一个单元格展示哪个字段
     */
    private static int dealSheet0(Workbook wb, Sheet sheet, int startRow, int startCol) {
        int sourceRow = startRow;
        Row rowHead = buildRow(sheet, startRow);
        String[] titles = {"时间", "支出(元)", "实收(元)", "收益(元)"};
        for (int i= 0; i<titles.length; i++) {
            Cell cellTitle = rowHead.createCell(startCol + i);
            cellTitle.setCellStyle(CellStyleUtils.buildStyleForTitle2(wb));
            cellTitle.setCellValue(titles[i]);
        }
        startRow ++;

        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        BusinessReportEntity businessReportEntity = DataFactoryUtils.buildBusinessReportEntity();
        for (BusinessReportEntity.BusinessReportItemEntity item : businessReportEntity.getItemList()) {
            Row rowBody = buildRow(sheet, startRow);
            rowBody.createCell(startCol).setCellValue(fmt.format(item.getDate()));
            rowBody.createCell(startCol + 1).setCellValue(getShowDoublePrice(item.getCost()));
            rowBody.createCell(startCol + 2).setCellValue(getShowDoublePrice(item.getReceipt()));
            rowBody.createCell(startCol + 3).setCellValue(getShowDoublePrice(item.getIncome()));
            startRow ++;
        }
        Row rowFoot = buildRow(sheet, startRow);
        Cell cellFoot1 = rowFoot.createCell(startCol);
        cellFoot1.setCellStyle(CellStyleUtils.buildStyleForTitle3(wb));
        cellFoot1.setCellValue("合计");
        rowFoot.createCell(startCol + 1).setCellValue(getShowDoublePrice(businessReportEntity.getCost()));
        rowFoot.createCell(startCol + 2).setCellValue(getShowDoublePrice(businessReportEntity.getReceipt()));
        rowFoot.createCell(startCol + 3).setCellValue(getShowDoublePrice(businessReportEntity.getIncome()));
        startRow ++;

        return startRow - sourceRow;
    }

}
