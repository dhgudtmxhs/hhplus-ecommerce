package kr.hhplus.be.server.payment;

import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.domain.PaymentStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentTest {

    @Test
    void 결제생성시_정상적으로_생성된다() {
        // Given
        Long orderId = 1L;
        Long orderPrice = 10000L;
        Long finalPrice = 8000L;
        Long couponId = 123L;

        // When
        Payment payment = Payment.create(orderId, orderPrice, finalPrice, couponId);

        // Then
        assertNotNull(payment);
        assertEquals(orderId, payment.getOrderId());
        assertEquals(orderPrice, payment.getOrderPrice());
        assertEquals(finalPrice, payment.getFinalPrice());
        assertEquals(couponId, payment.getCouponId());
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
    }

    @Test
    void 결제금액이_음수일경우_예외가_발생한다() {
        // Given
        Long orderId = 1L;
        Long orderPrice = -10000L; // Invalid order price
        Long finalPrice = 8000L;
        Long couponId = 123L;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Payment.create(orderId, orderPrice, finalPrice, couponId)
        );
    }

    @Test
    void 최종결제금액이_음수일경우_예외가_발생한다() {
        // Given
        Long orderId = 1L;
        Long orderPrice = 10000L;
        Long finalPrice = -5000L; // Invalid final price
        Long couponId = 123L;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Payment.create(orderId, orderPrice, finalPrice, couponId)
        );
    }

    @Test
    void 쿠폰없이_결제가_정상적으로_생성된다() {
        // Given
        Long orderId = 2L;
        Long orderPrice = 5000L;
        Long finalPrice = 5000L;
        Long couponId = null; // No coupon

        // When
        Payment payment = Payment.create(orderId, orderPrice, finalPrice, couponId);

        // Then
        assertNotNull(payment);
        assertEquals(orderId, payment.getOrderId());
        assertEquals(orderPrice, payment.getOrderPrice());
        assertEquals(finalPrice, payment.getFinalPrice());
        assertNull(payment.getCouponId());
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
    }

    @Test
    void 결제상태는_성공으로_초기화된다() {
        // Given
        Long orderId = 3L;
        Long orderPrice = 15000L;
        Long finalPrice = 10000L;
        Long couponId = 456L;

        // When
        Payment payment = Payment.create(orderId, orderPrice, finalPrice, couponId);

        // Then
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
    }
}
