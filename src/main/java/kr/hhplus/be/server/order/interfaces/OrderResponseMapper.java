package kr.hhplus.be.server.order.interfaces;

import kr.hhplus.be.server.order.application.OrderInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderResponseMapper {

    OrderResponse toOrderResponse(OrderInfo orderInfo);
}