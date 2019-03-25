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
public class OrderDetailEntity {

    private String orderNo;
    private Date createTime;    // 下单日期
    private Long realPay;   // 实付
    private String buyerName;   // 购买人
    private String buyerMobile;
    private String cashierName; // 收银员
    private String payChannelName;  // 支付渠道

    private List<OrderItemEntity> itemList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemEntity {
        private String itemName;    // 商品名称
        private Integer number;     // 购买数量
        private Long originPrice;   // 原价，单位分
    }

}
