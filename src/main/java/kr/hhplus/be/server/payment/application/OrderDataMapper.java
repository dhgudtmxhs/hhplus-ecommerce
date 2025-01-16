package kr.hhplus.be.server.payment.application;

import kr.hhplus.be.server.order.domain.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderDataMapper {

    @Mapping(source = "id", target = "orderId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "totalPrice", target = "totalPrice")
    @Mapping(target = "status", expression = "java(order.getStatus().name())") // Enum to String
    OrderData toOrderData(Order order);
}