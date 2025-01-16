package kr.hhplus.be.server.payment.interfaces;

import kr.hhplus.be.server.payment.application.PaymentInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentResponseMapper {
    PaymentResponse toPaymentResponse(PaymentInfo paymentInfo);
}