package com.zxy.demo.excel.demo2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;


/**
 * 构造数据
 */
public class DataFactoryUtils {

    // 构造数据
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

}
