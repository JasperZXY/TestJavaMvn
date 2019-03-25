package com.zxy.demo.excel.demo2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


/**
 * 构造数据
 */
public class DataFactoryUtils {

    public static BusinessReportEntity buildBusinessReportEntity() {
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

    public static List<OrderDetailEntity> buildOrderDetailEntityList() {
        Random random = new Random();
        List<OrderDetailEntity> list= new LinkedList<>();
        for (int i=0; i<20; i++) {
            OrderDetailEntity orderDetailEntity = new OrderDetailEntity();
            orderDetailEntity.setBuyerMobile(genMobile());
            orderDetailEntity.setBuyerName(genUserName());
            orderDetailEntity.setCashierName(genUserName());
            orderDetailEntity.setCreateTime(new Date());
            orderDetailEntity.setRealPay((long) random.nextInt(100000));
            orderDetailEntity.setPayChannelName("支付宝");
            orderDetailEntity.setOrderNo(genOrderNo());
            list.add(orderDetailEntity);

            orderDetailEntity.setItemList(new ArrayList<>());
            for (int j=0; j<random.nextInt(3) + 1; j++) {
                OrderDetailEntity.OrderItemEntity item = new OrderDetailEntity.OrderItemEntity();
                item.setItemName(genProductName());
                item.setNumber(random.nextInt(5) + 1);
                item.setOriginPrice((long) random.nextInt(100000));
                orderDetailEntity.getItemList().add(item);
            }
        }

        return list;
    }

    public static String genUserName() {
        List<String> familyNames = Arrays.asList("李", "张", "朱", "赵", "刘", "司马");
        List<String> names = Arrays.asList("晓", "鹏", "泽", "亮", "辉", "龙", "杰", "夏");
        Random random = new Random();
        return familyNames.get(random.nextInt(familyNames.size())) + names.get(random.nextInt(names.size())) + names.get(random.nextInt(names.size()));
    }

    public static String genMobile() {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        builder.append("1");
        for (int i=0; i<10; i++) {
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }

    public static String genOrderNo() {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        builder.append("E");
        for (int i=0; i<15; i++) {
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }

    public static String genProductName()  {
        List<String> names = Arrays.asList("洗发水", "毛巾", "牙刷", "牙膏", "拖把", "桶");
        Random random = new Random();
        return names.get(random.nextInt(names.size()));
    }

}
