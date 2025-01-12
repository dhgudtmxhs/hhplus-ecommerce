package kr.hhplus.be.server.payment;

import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.domain.PaymentMethod;
import kr.hhplus.be.server.payment.domain.PaymentStatus;
import kr.hhplus.be.server.point.domain.Point;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentTest {

    @Test
    void 포인트가_결제금액보다_많거나_같으면_결제가_성공한다() {
        // Given
        Long orderId = 1L;
        Long finalPrice = 1000L;
        PaymentMethod paymentMethod = PaymentMethod.POINT;
        Point point = new Point(1L, 1L, 1500L); // 포인트가 결제 금액보다 많음

        // When
        Payment payment = Payment.create(orderId, finalPrice, paymentMethod, point);

        // Then
        assertEquals(PaymentStatus.SUCCESS, payment.status());
    }

    @Test
    void 포인트가_결제금액보다_적으면_결제가_실패한다() {
        // Given
        Long orderId = 1L;
        Long finalPrice = 1000L;
        PaymentMethod paymentMethod = PaymentMethod.POINT;
        Point point = new Point(1L, 1L, 500L); // 포인트가 결제 금액보다 적음

        // When
        Payment payment = Payment.create(orderId, finalPrice, paymentMethod, point);

        // Then
        assertEquals(PaymentStatus.FAILED, payment.status());
    }

    @Test
    void 결제금액이_0원이면_결제가_성공한다() {
        // Given
        Long orderId = 1L;
        Long finalPrice = 0L;
        PaymentMethod paymentMethod = PaymentMethod.POINT;
        Point point = new Point(1L, 1L, 500L); // 포인트가 결제 금액보다 많음

        // When
        Payment payment = Payment.create(orderId, finalPrice, paymentMethod, point);

        // Then
        assertEquals(PaymentStatus.SUCCESS, payment.status());
    }
}