package kr.hhplus.be.server.order.application;
import kr.hhplus.be.server.order.domain.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderInfoMapper {

    @Mapping(source = "order.id", target = "id")
    @Mapping(source = "order.userId", target = "userId")
    @Mapping(source = "order.totalPrice", target = "totalPrice")
    @Mapping(source = "order.status", target = "status")
    @Mapping(source = "orderItemInfos", target = "orderItems")
    OrderInfo toOrderInfo(Order order, List<OrderItemInfo> orderItemInfos);

    @Mapping(source = "orderItemData.productId", target = "productId")
    @Mapping(source = "orderItemData.productName", target = "productName")
    @Mapping(source = "orderItemData.quantity", target = "quantity")
    @Mapping(source = "orderItemData.price", target = "price")
    OrderItemInfo toOrderItemInfo(OrderItemData orderItemData);
}