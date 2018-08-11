package com.zxy.demo.excel;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

public class TestRead {
    public static void main(String[] args) {
        try {
            FileReader fr = new FileReader("F:\\\\tmp\\uid\\uid.csv");
            BufferedReader br = new BufferedReader(fr);
            System.out.println(br.readLine());
            System.out.println(br.readLine());
            System.out.println(br.readLine());
            System.out.println(br.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        }
//        try {
//            WorkbookSettings wbs = new WorkbookSettings();
//            Workbook wb = Workbook.getWorkbook(new FileInputStream("F:\\\\tmp\\uid\\uid.xls"), wbs);
//            Sheet[] sheets = wb.getSheets();
//
//            System.out.println("行数：" + sheets[0].getRows());
//
//            Sheet sheet = sheets[0];
//            for (int i = 0; i < sheet.getRows(); i++) {
//                System.out.println(sheet.getRow(i)[0].getContents());
//            }
//
//            wb.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
