package kr.hhplus.be.server.payment.domain;

import kr.hhplus.be.server.order.domain.Order;
import kr.hhplus.be.server.point.domain.Point;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment processPayment(Order order, Point point) {
        // Payment 도메인에서 결제 상태 설정과 검증
        Payment payment = Payment.create(order.getId(), order.getFinalPrice(), PaymentMethod.POINT, point);

        // Payment 저장
        return paymentRepository.save(payment);
    }
}