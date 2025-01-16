package kr.hhplus.be.server.payment;

import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.domain.PaymentRepository;
import kr.hhplus.be.server.payment.domain.PaymentService;
import kr.hhplus.be.server.payment.domain.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    void 결제를_정상적으로_생성하고_저장한다() {
        // Given
        Long orderId = 1L;
        Long orderPrice = 1000L;
        Long finalPrice = 800L;
        Long couponId = 101L;

        Payment expectedPayment = Payment.builder()
                .orderId(orderId)
                .orderPrice(orderPrice)
                .finalPrice(finalPrice)
                .couponId(couponId)
                .status(PaymentStatus.SUCCESS)
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(expectedPayment);

        // When
        Payment result = paymentService.createPayment(orderId, orderPrice, finalPrice, couponId);

        // Then
        assertNotNull(result);
        assertEquals(orderId, result.getOrderId());
        assertEquals(orderPrice, result.getOrderPrice());
        assertEquals(finalPrice, result.getFinalPrice());
        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void 결제금액이_음수일_경우_예외를_발생시킨다() {
        // Given
        Long orderId = 1L;
        Long orderPrice = 1000L;
        Long finalPrice = -500L; // Invalid final price
        Long couponId = 101L;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.createPayment(orderId, orderPrice, finalPrice, couponId);
        });

        verify(paymentRepository, never()).save(any(Payment.class));
    }
}