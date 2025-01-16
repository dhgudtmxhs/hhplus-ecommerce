package kr.hhplus.be.server.payment.interfaces;

import kr.hhplus.be.server.payment.application.PaymentCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentRequestMapper {
    PaymentCommand toPaymentCommand(PaymentRequest request);
}