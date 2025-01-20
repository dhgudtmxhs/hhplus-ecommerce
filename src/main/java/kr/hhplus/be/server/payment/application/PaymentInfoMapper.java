package kr.hhplus.be.server.payment.application;

import kr.hhplus.be.server.payment.domain.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentInfoMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "orderId", target = "orderId")
    @Mapping(source = "orderPrice", target = "orderPrice")
    @Mapping(source = "finalPrice", target = "finalPrice")
    @Mapping(target = "status", expression = "java(payment.getStatus().name())") // Enum to String
    PaymentInfo toPaymentInfo(Payment payment);
}
