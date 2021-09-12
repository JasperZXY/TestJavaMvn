package com.zxy.demo.mapstruct.dos;

import lombok.Data;

@Data
public class OrderItemDO {

    private Long itemId;

    private String itemName;

    private Long price;

}
