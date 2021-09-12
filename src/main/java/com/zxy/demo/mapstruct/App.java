package com.zxy.demo.mapstruct;

import com.alibaba.fastjson.JSON;
import com.zxy.demo.mapstruct.converter.OrderConverter;
import com.zxy.demo.mapstruct.dos.OrderDO;
import com.zxy.demo.mapstruct.dos.OrderItemDO;
import com.zxy.demo.mapstruct.dto.OrderDTO;
import com.zxy.demo.mapstruct.dto.OrderExtInfo;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * https://mapstruct.org/
 *
 * MapStruct是一个代码生成器，它基于约定优于配置的方法，
 * 极大地简化了Java bean类型之间映射的实现。
 *
 * 在编译期间会生成接口的实现类（xxxImpl），里面会对Java bean做映射。
 */
public class App {

    @Autowired
    private OrderConverter orderConverter;

    public static void main(String[] args) {
        OrderConverter orderConverter = Mappers.getMapper(OrderConverter.class);

        OrderExtInfo extInfo = new OrderExtInfo();
        extInfo.setExt1("abc");
        extInfo.setExt2("123");

        OrderDO orderDO = new OrderDO();
        orderDO.setOrderNo("123456");
        orderDO.setPrice(300L);
        orderDO.setExtInfo(JSON.toJSONString(extInfo));

        OrderItemDO orderItemDO1 = new OrderItemDO();
        orderItemDO1.setItemId(1L);
        orderItemDO1.setItemName("沐浴露");
        orderItemDO1.setPrice(100L);

        OrderItemDO orderItemDO2 = new OrderItemDO();
        orderItemDO2.setItemId(2L);
        orderItemDO2.setItemName("洗发水");
        orderItemDO2.setPrice(200L);

        List<OrderItemDO> orderItemDOList = Arrays.asList(orderItemDO1, orderItemDO2);

        OrderDTO orderDTO = orderConverter.toOrderDTO(orderDO, orderItemDOList);
        System.out.println(orderDTO);
    }
}
