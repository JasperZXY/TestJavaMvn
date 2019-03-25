package com.zxy.demo.excel.demo2;

import com.zxy.demo.excel.ConfigUtils;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/**
 * 这个demo导出的文件是".xlsx"，xlsx是从Office2007开始使用的。
 *
 * 一般处理Excel导出，是会清楚地知道要导出表头，但具体多少行数据可能需要实时计算，所以下面的方法处理上有startRow，但没有startColumn
 */
public class ExcelDemo2 {

    public static void main(String[] args) {

        Workbook wb = new SXSSFWorkbook();
        Sheet sheet0 = wb.createSheet("销售报表");
        Sheet sheet1 = wb.createSheet("订单报表");
        Sheet sheet2 = wb.createSheet("颜色画板");

        SheetData0.writeData(wb, sheet0);
        SheetData1.writeData(wb, sheet1);
        dealSheet2(wb, sheet2);

        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String exportFile = ConfigUtils.ROOT_PATH + "/demo-" + fmt.format(new Date()) + ".xlsx";
        System.out.println("导出路径：" + exportFile);
        try(FileOutputStream fileOutputStream = new FileOutputStream(exportFile)) {
            wb.write(fileOutputStream);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 把Excel能支持的所有颜色进行展示
    private static void dealSheet2(Workbook wb, Sheet sheet) {
        sheet.setColumnWidth(0, 5000);
        sheet.setColumnWidth(1, 8000);
        int rownum = 0;
        List<IndexedColors> list = Arrays.asList(IndexedColors.values());
        list.sort(Comparator.comparing(Enum::name));
        for (IndexedColors indexedColors : list) {
            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setFillForegroundColor(indexedColors.getIndex());
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);

            Row row = sheet.createRow(rownum);
            row.createCell(0).setCellStyle(cellStyle);
            row.createCell(1).setCellValue(indexedColors.name());

            rownum ++;
        }
    }


}
