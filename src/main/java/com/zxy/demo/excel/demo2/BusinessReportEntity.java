package com.zxy.demo.excel.demo2;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessReportEntity {

    private Long cost;      // 成本，负数
    private Long receipt;   // 实收，正数
    private Long income;    // 收益
    private List<BusinessReportItemEntity> itemList;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusinessReportItemEntity {
        private Date date;
        private Long cost;
        private Long receipt;
        private Long income;
    }
}
