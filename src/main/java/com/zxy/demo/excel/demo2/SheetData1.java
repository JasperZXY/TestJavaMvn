package com.zxy.demo.excel.demo2;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;


/**
 * 这个例子主要是说明字段比较多的时候的处理，单元格的宽度，哪几行需要合并，都需要跟表头保持对应
 */
public class SheetData1 {

    public static void writeData(Workbook wb, Sheet sheet) {
        int startRow = 0;

        // 调整单元格宽度
        sheet.setColumnWidth(0, 4200);
        sheet.setColumnWidth(1, 5200);
        sheet.setColumnWidth(2, 2500);
        sheet.setColumnWidth(3, 2500);
        sheet.setColumnWidth(4, 3200);
        sheet.setColumnWidth(5, 3000);
        sheet.setColumnWidth(6, 2000);
        sheet.setColumnWidth(7, 1500);
        sheet.setColumnWidth(8, 3000);

        CellStyle cellStyle = CellStyleUtils.buildStyleForTitle2(wb);

        String[] titles = {"订单号", "下单时间", "收银员", "购买人", "购买人手机号", "实付(元)", "商品", "数量", "原价(元)"};
        Row rowForTitle = sheet.createRow(startRow);
        for (int i=0; i<titles.length; i++) {
            Utils.buildCell(rowForTitle, i, cellStyle).setCellValue(titles[i]);
        }
        startRow ++;

        writeData(wb, sheet, startRow);
    }

    /*
    对于这种展示的字段比较多的，一般选择按竖列进行拆分，所以就不像其他的一样返回当前的行数
     */
    private static void writeData(Workbook wb, Sheet sheet, int startRow) {
        List<OrderDetailEntity> list = DataFactoryUtils.buildOrderDetailEntityList();
        for (OrderDetailEntity orderDetailEntity : list) {

            for (int itemIndex=0; itemIndex<orderDetailEntity.getItemList().size(); itemIndex++) {
                Row row = Utils.buildRow(sheet, startRow);
                // 这里的代码就比较恶心了，是跟着title来的
                if (itemIndex == 0) {
                    Utils.buildCell(row, 0, null).setCellValue(orderDetailEntity.getOrderNo());
                    Utils.buildCell(row, 1, null).setCellValue(Utils.getShowTime(orderDetailEntity.getCreateTime()));
                    Utils.buildCell(row, 2, null).setCellValue(orderDetailEntity.getCashierName());
                    Utils.buildCell(row, 3, null).setCellValue(orderDetailEntity.getBuyerName());
                    Utils.buildCell(row, 4, null).setCellValue(orderDetailEntity.getBuyerMobile());
                    Utils.buildCell(row, 5, null).setCellValue(Utils.getShowDoublePrice(orderDetailEntity.getRealPay()) + "元");
                }
                Utils.buildCell(row, 6, null).setCellValue(orderDetailEntity.getItemList().get(itemIndex).getItemName());
                Utils.buildCell(row, 7, null).setCellValue(orderDetailEntity.getItemList().get(itemIndex).getNumber());
                Utils.buildCell(row, 8, null).setCellValue(Utils.getShowDoublePrice(orderDetailEntity.getItemList().get(itemIndex).getOriginPrice()) + "元");

                startRow ++;
            }

            if (orderDetailEntity.getItemList().size() > 1) {
                for (int col=0; col<=5; col++) {
                    // 这一部分跟上面的 if (itemIndex == 0) 保持一致
                    Utils.mergeRow(sheet, startRow - orderDetailEntity.getItemList().size(), startRow - 1, col);
                }
            }
        }
    }

}
