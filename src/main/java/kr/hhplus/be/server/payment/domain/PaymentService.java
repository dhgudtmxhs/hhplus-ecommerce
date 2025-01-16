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

    public Payment createPayment(Long orderId, Long orderPrice, Long finalPrice, Long couponId) {
        Payment payment = Payment.create(orderId, orderPrice, finalPrice, couponId);

        paymentRepository.save(payment);

        return payment;
    }
}