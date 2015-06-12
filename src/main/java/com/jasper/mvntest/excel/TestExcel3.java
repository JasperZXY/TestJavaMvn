package com.jasper.mvntest.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.jasper.mvntest.util.TimeUtil;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class TestExcel3 {

    public static void main(String[] args) {
        try {

            write("F:\\\\tmp\\消息通道统计-5月份.xls", 
                    "jdbc:mysql://14.17.107.112:6303/service_popup?useUnicode=true&amp;characterEncoding=utf8&amp;autoReconnect=true&amp;zeroDateTimeBehavior=convertToNull", 
                    "service_popup", "LbalVg1B8V");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void write(String filePath, String url, String username, String password) {
        try {
            WritableWorkbook book = Workbook.createWorkbook(new File(filePath));
            // 参数0表示这是第一页
            WritableSheet sheet0 = book.createSheet("汇总", 0);
            List<String> titleListSum = Arrays.asList("appId", "业务", "msgId", "日期", "到达量", "曝光量", "点击数", "点击用户数");
            List<List<String>> dataListSum = getDataFromDB(url, username, password);
            for (int i = 0; i < titleListSum.size(); i++) {
                Label label = new Label(i, 0, titleListSum.get(i));
                sheet0.addCell(label);
            }

            for (int i = 0; i < dataListSum.size(); i++) {
                for (int j = 0; j < dataListSum.get(i).size(); j++) {
                    Label label = new Label(j, i + 1, dataListSum.get(i).get(j));
                    sheet0.addCell(label);
//                    if (j == 1 || j == 2) {
//                        Label label = new Label(j, i + 1, dataListSum.get(i).get(j));
//                        sheet0.addCell(label);
//                    } else {
//                        jxl.write.Number number = new jxl.write.Number(j, i + 1, Long.parseLong(dataListSum.get(i).get(j)));
//                        sheet0.addCell(number);
//                    }
                }
            }
            
            // 写入数据并关闭文件
            book.write();
            book.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<List<String>> getDataFromDB(String url, String username, String password)
            throws ParseException {

        List<List<String>> list = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // 加载驱动
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, username, password);
            // 通过Connection对象创建Statement对象
            // stmt = conn.createStatement();
            stmt = conn.prepareStatement("select * from popupLog where appId=1001 and popupDate > '2015-05-01'");
            rs = stmt.executeQuery();
            // 操作结果集
            while (rs.next()) {
                List<String> item = new ArrayList<>();
                item.add(rs.getString("appId"));
                if ("1001".equals(rs.getString("appId"))) {
                    item.add("页游");
                } else if("1002".equals(rs.getString("appId"))) {
                    item.add("端游");
                } else {
                    item.add("其他");
                }
                item.add(rs.getString("msgId"));
                item.add(rs.getString("popupDate"));
                if (rs.getString("popupCount") == null) {
                    item.add("0");
                } else {
                    item.add(rs.getString("popupCount"));
                }
                if (rs.getString("viewCount") == null) {
                    item.add("0");
                } else {
                    item.add(rs.getString("viewCount"));
                }
                if (rs.getString("clickCount") == null) {
                    item.add("0");
                } else {
                    item.add(rs.getString("clickCount"));
                }
                if (rs.getString("clickUserCount") == null) {
                    item.add("0");
                } else {
                    item.add(rs.getString("clickUserCount"));
                }
                list.add(item);
            }
            System.out.println(list);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlClose(rs);
            sqlClose(stmt);
            sqlClose(conn);
        }

        return list;
    }

    public static void sqlClose(AutoCloseable obj) {
        if (obj != null) {
            try {
                obj.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static int str2int(String str, int defNum) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defNum;
    }

}
