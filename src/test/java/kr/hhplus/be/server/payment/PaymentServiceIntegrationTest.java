package kr.hhplus.be.server.payment;

import kr.hhplus.be.server.order.domain.OrderStatus;
import kr.hhplus.be.server.order.infra.OrderJpaRepository;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.domain.PaymentRepository;
import kr.hhplus.be.server.payment.domain.PaymentService;
import kr.hhplus.be.server.payment.domain.PaymentStatus;
import kr.hhplus.be.server.payment.infra.PaymentJpaRepository;
import kr.hhplus.be.server.point.domain.Point;
import kr.hhplus.be.server.order.domain.Order;
import org.junit.jupiter.api.BeforeEach;
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
    private PaymentJpaRepository paymentJpaRepository;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    private Order order;
    private Point point;

    @BeforeEach
    void setUp() {
        // 주문 데이터 생성
        order = Order.builder()
                .userId(1L)
                .totalPrice(1000L)
                .status(OrderStatus.CREATED)
                .build();

        // 데이터베이스에 저장
        order = orderJpaRepository.save(order);

        // 포인트 데이터 생성
        point = new Point(null, 1L, 1500L);
    }

    @Test
    void 충분한_포인트로_결제하면_결제가_성공하고_저장된다() {
        // When
        Payment result = paymentService.createPayment(order.getId(), order.getTotalPrice(), 800L, null);

        // Then
        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
        assertNotNull(result.getId());

        // 저장된 데이터 확인
        Payment savedPayment = paymentJpaRepository.findById(result.getId())
                .orElseThrow(() -> new AssertionError("아이디 없음"));
        assertEquals(order.getId(), savedPayment.getOrderId());
        assertEquals(800L, savedPayment.getFinalPrice());
    }

}