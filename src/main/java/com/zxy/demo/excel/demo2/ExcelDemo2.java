package com.zxy.demo.excel.demo2;

import com.zxy.demo.excel.ConfigUtils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;


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

        dealSheet0(wb, sheet0);
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

    private static void dealSheet0(Workbook wb, Sheet sheet) {
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
        BusinessReportEntity businessReportEntity = buildBusinessReportEntity();
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

    private static Row buildRow(Sheet sheet, int rownum) {
        Row row = sheet.getRow(rownum);
        if (row == null) {
            row = sheet.createRow(rownum);
        }
        return row;
    }

    // 构造数据
    private static BusinessReportEntity buildBusinessReportEntity() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long time = calendar.getTimeInMillis();

        BusinessReportEntity businessReportEntity = new BusinessReportEntity();
        businessReportEntity.setItemList(new ArrayList<>());

        Random random = new Random();
        long secondInDay = 24 * 60 * 60 * 1000;
        for (int i=1; i<20; i++) {
            BusinessReportEntity.BusinessReportItemEntity item =
                    BusinessReportEntity.BusinessReportItemEntity.builder()
                            .date(new Date(time - i * secondInDay))
                            .cost(Math.abs(random.nextLong()) % 100_00 * -1)
                            .receipt(Math.abs(random.nextLong()) % 300_00)
                            .build();
            item.setIncome(item.getReceipt() + item.getCost());
            businessReportEntity.getItemList().add(item);
        }

        businessReportEntity.setCost(businessReportEntity.getItemList().stream().mapToLong(BusinessReportEntity.BusinessReportItemEntity::getCost).sum());
        businessReportEntity.setReceipt(businessReportEntity.getItemList().stream().mapToLong(BusinessReportEntity.BusinessReportItemEntity::getReceipt).sum());
        businessReportEntity.setIncome(businessReportEntity.getItemList().stream().mapToLong(BusinessReportEntity.BusinessReportItemEntity::getIncome).sum());

        return businessReportEntity;
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

    // 价格的展示，如果是外界提供的接口，很有可能是精确到分（Long类型），这里保留两位小数点，然后精确到元
    public static Double getShowDoublePrice(Number price) {
        if (price == null) {
            return 0.00;
        }
        BigDecimal bigDecimal = new BigDecimal(price.doubleValue());
        BigDecimal result = bigDecimal.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
        return result.doubleValue();
    }


}
