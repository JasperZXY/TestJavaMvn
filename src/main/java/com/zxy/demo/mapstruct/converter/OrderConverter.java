package com.zxy.demo.mapstruct.converter;

import com.zxy.demo.mapstruct.dos.OrderDO;
import com.zxy.demo.mapstruct.dos.OrderItemDO;
import com.zxy.demo.mapstruct.dto.OrderDTO;
import com.zxy.demo.mapstruct.dto.OrderExtInfo;
import com.zxy.demo.mapstruct.dto.OrderItemDTO;

import java.util.List;

// 默认的方式，使用 Mappers.getMapper(Class) 来进行获取 Mapper
@org.mapstruct.Mapper(componentModel = "default")
// 交给spring管理，生成的impl会带上“@Component”，然后就可以用“@Autowired”来进行注入
//@org.mapstruct.Mapper(componentModel = "spring")
public interface OrderConverter {

    @org.mapstruct.Mapping(target = "totalPrice", source = "orderDO.price")
    @org.mapstruct.Mapping(target = "extInfo", expression = "java(toOrderExtInfo(orderDO.getExtInfo()))")
    @org.mapstruct.Mapping(target = "itemList", source = "itemDOList")
    OrderDTO toOrderDTO(OrderDO orderDO, List<OrderItemDO> itemDOList);

    OrderItemDTO toOrderItemDTO(OrderItemDO orderItemDO);

    List<OrderItemDTO> toOrderItemDTOList(List<OrderItemDO> orderItemDOList);

    default OrderExtInfo toOrderExtInfo(String extInfo) {
        if (extInfo == null) {
            return null;
        }
        return com.alibaba.fastjson.JSON.parseObject(extInfo, OrderExtInfo.class);
    }

}
