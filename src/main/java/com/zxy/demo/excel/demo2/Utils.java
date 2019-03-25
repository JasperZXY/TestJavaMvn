package com.zxy.demo.excel.demo2;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 *
 */
public class Utils {

    // 价格的展示，如果是外界提供的接口，很有可能是精确到分（Long类型），这里保留两位小数点，然后精确到元
    public static Double getShowDoublePrice(Number price) {
        if (price == null) {
            return 0.00;
        }
        BigDecimal bigDecimal = new BigDecimal(price.doubleValue());
        BigDecimal result = bigDecimal.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
        return result.doubleValue();
    }

    public static String getShowTime(Date date) {
        if (date == null) {
            return "-";
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    public static Row buildRow(Sheet sheet, int rownum) {
        Row row = sheet.getRow(rownum);
        if (row == null) {
            row = sheet.createRow(rownum);
        }
        return row;
    }

    public static Cell buildCell(Row row, int column, CellStyle cellStyle) {
        Cell cell = row.createCell(column);
        cell.setCellStyle(cellStyle);
        return cell;
    }

    public static void mergeRow(Sheet sheet, int firstRow, int lastRow, int col) {
        sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, col, col));
    }

}
