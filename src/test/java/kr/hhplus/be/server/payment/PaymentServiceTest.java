package kr.hhplus.be.server.payment;

import kr.hhplus.be.server.order.domain.Order;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.domain.PaymentMethod;
import kr.hhplus.be.server.payment.domain.PaymentRepository;
import kr.hhplus.be.server.payment.domain.PaymentService;
import kr.hhplus.be.server.payment.domain.PaymentStatus;
import kr.hhplus.be.server.point.domain.Point;
import kr.hhplus.be.server.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 충분한_포인트로_결제하면_결제가_성공하고_저장된다() {
        // Given
        Order order = new Order(1L, 1L, 1000L, 1000L, null, null, null);
        Point point = new Point(1L, 1L, 1500L);
        Payment expectedPayment = new Payment(null, order.getId(), order.getFinalPrice(), PaymentMethod.POINT, PaymentStatus.SUCCESS, point.getPoint());

        when(paymentRepository.save(any(Payment.class))).thenReturn(expectedPayment);

        // When
        Payment result = paymentService.processPayment(order, point);

        // Then
        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void 부족한_포인트로_결제하면_결제가_실패하고_저장된다() {
        // Given
        Order order = new Order(1L, 1L, 1000L, 1000L, null, null, null);
        Point point = new Point(1L, 1L, 500L);
        Payment expectedPayment = new Payment(null, order.getId(), order.getFinalPrice(), PaymentMethod.POINT, PaymentStatus.FAILED, point.getPoint());

        when(paymentRepository.save(any(Payment.class))).thenReturn(expectedPayment);

        // When
        Payment result = paymentService.processPayment(order, point);

        // Then
        assertEquals(PaymentStatus.FAILED, result.getStatus());
        verify(paymentRepository).save(any(Payment.class));
    }
}