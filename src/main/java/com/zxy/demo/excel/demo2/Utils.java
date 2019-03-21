package com.zxy.demo.excel.demo2;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.math.BigDecimal;


/**
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

    public static Row buildRow(Sheet sheet, int rownum) {
        Row row = sheet.getRow(rownum);
        if (row == null) {
            row = sheet.createRow(rownum);
        }
        return row;
    }

}
