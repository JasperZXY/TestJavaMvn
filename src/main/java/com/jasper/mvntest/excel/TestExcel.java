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

public class TestExcel {
    private static final int DAY_MILLISECOND = 24 * 60 * 60 * 1000;

    public static void main(String[] args) {
        Properties prop = new Properties();
        try {
            InputStream in = new FileInputStream("F:\\\\data\\subscription_db.txt");
            prop.load(in);
            System.out.println(prop);

            write("F:\\\\tmp\\游戏公社弹窗数据汇总表15-22.xls", 
                    prop.getProperty("url"), prop.getProperty("username"), prop.getProperty("password"), 
                    "20150315", "20150322");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void write(String filePath, String url, String username, String password, String startTimeStr, String endTimeStr) {
        try {
            WritableWorkbook book = Workbook.createWorkbook(new File(filePath));
            // 参数0表示这是第一页
            WritableSheet sheet0 = book.createSheet("汇总", 0);
            List<String> titleListSum = Arrays.asList("日期", "发送数量", "点击数量", "取消预订", "关闭数量");
            List<List<String>> dataListSum = getDataFromDB(true, url, username, password, startTimeStr, endTimeStr);
            for (int i = 0; i < titleListSum.size(); i++) {
                Label label = new Label(i, 0, titleListSum.get(i));
                sheet0.addCell(label);
            }

            for (int i = 0; i < dataListSum.size(); i++) {
                Label label = new Label(0, i + 1, dataListSum.get(i).get(0));
                sheet0.addCell(label);
                for (int j = 1; j < dataListSum.get(i).size(); j++) {
                    jxl.write.Number number = new jxl.write.Number(j, i + 1, Long.parseLong(dataListSum.get(i).get(j)));
                    sheet0.addCell(number);
                }
            }
            
            
            WritableSheet sheet1 = book.createSheet("详细AID", 1);
            List<String> titleList = Arrays.asList("日期", "AID", "发送数量", "点击数量", "取消预订", "关闭数量");
            List<List<String>> dataList = getDataFromDB(false, url, username, password, startTimeStr, endTimeStr);

            for (int i = 0; i < titleList.size(); i++) {
                Label label = new Label(i, 0, titleList.get(i));
                sheet1.addCell(label);
            }
            for (int i = 0; i < dataList.size(); i++) {
                Label label = new Label(0, i + 1, dataList.get(i).get(0));
                sheet1.addCell(label);
                for (int j = 1; j < dataList.get(i).size(); j++) {
                    jxl.write.Number number = new jxl.write.Number(j, i + 1, Long.parseLong(dataList.get(i).get(j)));
                    sheet1.addCell(number);
                }
            }

            // 写入数据并关闭文件
            book.write();
            book.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<List<String>> getDataFromDB(boolean isSum, String url, String username, String password, String startTimeStr, String endTimeStr)
            throws ParseException {
        long startTime = TimeUtil.get(8, startTimeStr).getTime();
        long endTime = TimeUtil.get(8, endTimeStr).getTime();

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
            if (isSum) {
                stmt = conn
                        .prepareStatement("select sum(SuccessCount) as successCount, sum(UrlCloseCount) as clickCount, sum(CancelCloseCount) as cancelCount, sum(UserCloseCount) as closeCount from NotifyLog where Type = 12 and NotifyTime >= ? and NotifyTime < ? order by NotifyTime desc");
            } else {
                stmt = conn
                        .prepareStatement("select aid, sum(SuccessCount) as successCount, sum(UrlCloseCount) as clickCount, sum(CancelCloseCount) as cancelCount, sum(UserCloseCount) as closeCount from NotifyLog where Type = 12 and NotifyTime >= ? and NotifyTime < ? group by aid order by NotifyTime desc");
            }
            for (long time = startTime; time <= endTime; time += DAY_MILLISECOND) {
                stmt.setObject(1, new Date(time));
                stmt.setObject(2, new Date(time + DAY_MILLISECOND));

                rs = stmt.executeQuery();
                // 操作结果集
                while (rs.next()) {
                    List<String> item = new ArrayList<>();
                    item.add(TimeUtil.get(11, new Date(time)));
                    if (! isSum) {
                        item.add(rs.getString("aid"));
                    }
                    if (rs.getString("successCount") == null) {
                        item.add("0");
                    } else {
                        item.add(rs.getString("successCount"));
                    }
                    if (rs.getString("clickCount") == null) {
                        item.add("0");
                    } else {
                        item.add(rs.getString("clickCount"));
                    }
                    if (rs.getString("cancelCount") == null) {
                        item.add("0");
                    } else {
                        item.add(rs.getString("cancelCount"));
                    }
                    if (rs.getString("closeCount") == null) {
                        item.add("0");
                    } else {
                        item.add(rs.getString("closeCount"));
                    }
                    list.add(item);
                }
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
