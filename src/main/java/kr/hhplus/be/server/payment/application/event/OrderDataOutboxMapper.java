package kr.hhplus.be.server.payment.application.event;

import kr.hhplus.be.server.payment.infra.outbox.OrderDataOutbox;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderDataOutboxMapper {

    @Mapping(source = "orderId", target = "orderId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "totalPrice", target = "totalPrice")
    @Mapping(source = "orderStatus", target = "orderStatus")
    OrderDataMessage toMessage(OrderDataOutbox outbox);
}
