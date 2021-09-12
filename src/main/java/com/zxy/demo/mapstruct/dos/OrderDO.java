package com.zxy.demo.mapstruct.dos;

import lombok.Data;

@Data
public class OrderDO {
    private String orderNo;

    private Long price;

    private String extInfo;
}
