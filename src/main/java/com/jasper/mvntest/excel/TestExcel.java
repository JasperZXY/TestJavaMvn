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
            List<String> titleList = Arrays.asList("日期", "发送数量", "点击数量", "取消预订", "关闭数量");
            
            List<List<String>> dataList = getDataFromDB(prop.getProperty("url"), prop.getProperty("username"), prop.getProperty("password"), "20150201", "20150305");
            
            write("F:\\\\tmp\\游戏公社弹窗数据汇总表.xls", "统计数据", titleList, dataList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    public static void write(String filePath, String sheetName, List<String> titleList, List<List<String>> dataList) {
        try {
            WritableWorkbook book = Workbook.createWorkbook(new File(filePath));
            // 参数0表示这是第一页
            WritableSheet sheet = book.createSheet(sheetName, 0);
            int start = 0;
            
            if (titleList != null && titleList.size() > 0) {
                start = 1;
                for (int i=0; i<titleList.size(); i++) {
                    Label label = new Label(i, 0, titleList.get(i));
                    sheet.addCell(label);
                }
            }
            
            for (int i=0; i<dataList.size(); i++) {
                for (int j=0; j<dataList.get(i).size(); j++) {
//                    jxl.write.Number number = new jxl.write.Number(j, i+start, dataList.get(i).get(j).doubleValue());
                    Label label = new Label(j, i+start, dataList.get(i).get(j));
                    sheet.addCell(label);
                }
            }

            // 写入数据并关闭文件
            book.write();
            book.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public static List<List<String>> getDataFromDB(String url, String username, String password, String startTimeStr, String endTimeStr) throws ParseException {
        long startTime = TimeUtil.get(8, startTimeStr).getTime();
        long endTime = TimeUtil.get(8, endTimeStr).getTime();
        
        List<List<String>> list = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            //加载驱动
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(
                    url, username, password);
            //通过Connection对象创建Statement对象
//            stmt = conn.createStatement();
            stmt = conn.prepareStatement("select sum(SuccessCount) as successCount, sum(UrlCloseCount) as clickCount, sum(CancelCloseCount) as cancelCount, sum(UserCloseCount) as closeCount from NotifyLog where Type = 12 and NotifyTime >= ? and NotifyTime < ? order by NotifyTime desc");
            for (long time=startTime; time<=endTime; time+=DAY_MILLISECOND) {
                stmt.setObject(1, new Date(time));
                stmt.setObject(2, new Date(time+DAY_MILLISECOND));
                
                rs = stmt.executeQuery();
                //操作结果集
                while(rs.next()) {
                    List<String> item = new ArrayList<>();
                    item.add(TimeUtil.get(8, new Date(time)));
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
