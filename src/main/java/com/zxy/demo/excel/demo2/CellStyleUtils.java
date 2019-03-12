package com.zxy.demo.excel.demo2;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;


/**
 */
public class CellStyleUtils {

    /**
     * 用于表头，36号字体，垂直水平居中
     */
    public static CellStyle buildStyleForTitle1(Workbook wb) {
        CellStyle cellStyle = wb.createCellStyle();

        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 36);
        font.setBold(true);
        cellStyle.setFont(font);

        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        return cellStyle;
    }

    /**
     * 用于表头，粗体，垂直水平居中
     */
    public static CellStyle buildStyleForTitle2(Workbook wb) {
        CellStyle cellStyle = wb.createCellStyle();

        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        Font font = wb.createFont();
        font.setBold(true);
        cellStyle.setFont(font);

        cellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cellStyle;
    }

    public static CellStyle buildStyleForTitle3(Workbook wb) {
        CellStyle cellStyle = wb.createCellStyle();

        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        Font font = wb.createFont();
        font.setBold(true);
        cellStyle.setFont(font);

        cellStyle.setFillForegroundColor(IndexedColors.ROSE.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cellStyle;
    }


    /**
     * 用于表头描述，一般可能会有多行，所以需要换行
     *
     * 换行还不够，还需要自己设置占用的行数，比如通过下面去让一行占用两行的高度
     * row.setHeight((short) (row.getHeight() * 2));
     */
    public static CellStyle buildStyleForTitleDesc(Workbook wb) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setWrapText(true);

        return cellStyle;
    }

}
