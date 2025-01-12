package kr.hhplus.be.server.payment.infra;

import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.domain.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {
    private final PaymentJpaRepository paymentJpaRepository; // JPA Repository
    private final PaymentMapper paymentMapper;               // MapStruct Mapper

    @Override
    public Payment save(Payment payment) {
        PaymentEntity savedEntity = paymentJpaRepository.save(paymentMapper.toEntity(payment));

        return paymentMapper.toDomain(savedEntity);
    }
}
