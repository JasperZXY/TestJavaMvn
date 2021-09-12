package com.zxy.demo.mapstruct.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderDTO implements Serializable {

    private String orderNo;

    private Long totalPrice;

    private List<OrderItemDTO> itemList;

    private OrderExtInfo extInfo;

}
