package kr.hhplus.be.server.payment.infra;

import kr.hhplus.be.server.payment.domain.Payment;
import org.mapstruct.Mapper;
import kr.hhplus.be.server.payment.infra.PaymentEntity;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentEntity toEntity(Payment payment);

    Payment toDomain(PaymentEntity paymentEntity);
}