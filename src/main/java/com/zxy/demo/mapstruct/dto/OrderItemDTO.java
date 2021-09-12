package com.zxy.demo.mapstruct.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderItemDTO implements Serializable {
    private Long itemId;

    private String itemName;

    private Long price;
}
