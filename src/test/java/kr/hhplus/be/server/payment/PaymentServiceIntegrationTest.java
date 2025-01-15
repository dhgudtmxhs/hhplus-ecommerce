package kr.hhplus.be.server.payment;

import kr.hhplus.be.server.order.domain.Order;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.domain.PaymentRepository;
import kr.hhplus.be.server.payment.domain.PaymentService;
import kr.hhplus.be.server.payment.domain.PaymentStatus;
import kr.hhplus.be.server.point.domain.Point;
import kr.hhplus.be.server.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class PaymentServiceIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void 충분한_포인트로_결제하면_결제가_성공하고_저장된다() {
        // Given
        Order order = new Order(1L, 1L, 1000L, 1000L, null, null, null);
        Point point = new Point(1L, 1L, 1500L);

        // When
        Payment result = paymentService.processPayment(order, point);

        // Then
        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
        assertNotNull(result.getId());  // 저장되어 ID가 생성되었는지 확인
    }

}