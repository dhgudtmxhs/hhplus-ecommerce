package kr.hhplus.be.server.order;

import kr.hhplus.be.server.coupon.domain.DiscountType;
import kr.hhplus.be.server.order.domain.Order;
import kr.hhplus.be.server.order.domain.OrderItem;
import kr.hhplus.be.server.order.domain.OrderStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    @Test
    void 유효한_상품과_쿠폰으로_주문을_생성하면_정상적으로_생성된다() {
        // Given
        List<OrderItem> orderItems = List.of(
                new OrderItem(null, 1L, 1000L, 2L), // 상품A 2개
                new OrderItem(null, 2L, 1500L, 1L)  // 상품B 1개
        );
        Long couponId = 1L;
        Long discountAmount = 500L;
        Long usedPoints = 200L;

        // When
        Order order = Order.create(1L, orderItems, couponId, discountAmount, usedPoints);

        // Then
        assertEquals(3500L, order.getTotalPrice());
        assertEquals(2800L, order.getFinalPrice());
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void 유효한_상품만으로_주문을_생성하면_정상적으로_생성된다() {
        // Given
        List<OrderItem> orderItems = List.of(
                new OrderItem(null, 1L, 1000L, 2L),
                new OrderItem(null, 2L, 1500L, 1L)
        );
        Long usedPoints = 0L;

        // When
        Order order = Order.create(1L, orderItems, null, 0L, usedPoints);

        // Then
        assertEquals(3500L, order.getTotalPrice());
        assertEquals(3500L, order.getFinalPrice());
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void 최종_결제_금액이_0보다_작으면_IllegalArgumentException_예외가_발생한다() {
        // Given
        List<OrderItem> orderItems = List.of(
                new OrderItem(null, 1L, 1000L, 1L)
        );
        Long discountAmount = 1500L; // 할인 금액이 상품 금액보다 큼
        Long usedPoints = 1000L;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> Order.create(1L, orderItems, 1L, discountAmount, usedPoints));
    }

    @Test
    void 주문_상태를_결제_완료로_변경하면_정상적으로_변경된다() {
        // Given
        List<OrderItem> orderItems = List.of(
                new OrderItem(null, 1L, 1000L, 1L)
        );
        Order order = Order.create(1L, orderItems, null, 0L, 0L);

        // When
        order.markAsPaid();

        // Then
        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    void 결제_완료된_주문을_취소하면_IllegalStateException_예외가_발생한다() {
        // Given
        List<OrderItem> orderItems = List.of(
                new OrderItem(null, 1L, 1000L, 1L)
        );
        Order order = Order.create(1L, orderItems, null, 0L, 0L);
        order.markAsPaid();

        // When & Then
        assertThrows(IllegalStateException.class, order::cancelOrder);
    }

    @Test
    void 결제_대기중인_주문을_취소하면_정상적으로_취소된다() {
        // Given
        List<OrderItem> orderItems = List.of(
                new OrderItem(null, 1L, 1000L, 1L)
        );
        Order order = Order.create(1L, orderItems, null, 0L, 0L);

        // When
        order.cancelOrder();

        // Then
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void 결제_대기중인_주문을_결제_실패로_변경하면_정상적으로_변경된다() {
        // Given
        List<OrderItem> orderItems = List.of(
                new OrderItem(null, 1L, 1000L, 1L)
        );
        Order order = Order.create(1L, orderItems, null, 0L, 0L);

        // When
        order.markAsFailed();

        // Then
        assertEquals(OrderStatus.FAILED, order.getStatus());
    }
}