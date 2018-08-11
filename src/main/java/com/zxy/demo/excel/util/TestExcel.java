package com.zxy.demo.excel.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestExcel {
    public static void main(String[] args) throws Exception {
        String filePath = System.getProperty("user.dir") + "\\src\\main\\resources\\Java操作excel-user.xls";
        System.out.println("filePath:" + filePath);
        final List<User> list = new ArrayList<>();

        ExcelUtils.read(User.class, filePath, false, new ExcelUtils.Reader<User>() {
            @Override
            public void readLine(int row, User data) {
                list.add(data);
            }

            @Override
            public void readError(int row, String message) {
                System.err.println("readError row:" + row + " message:" + message);
            }

            @Override
            public void readError(String message) {
                System.err.println("readError message:" + message);
            }
        });
        System.out.println("1:" + list);

        List<User> list2 = ExcelUtils.read(User.class, filePath);
        System.out.println("2:" + list2);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HHmmss");
        ExcelUtils.export(User.class, "E:\\tmp\\export" + simpleDateFormat.format(new Date()) + ".xls", list);
    }
}
