package kr.hhplus.be.server.order;

import kr.hhplus.be.server.coupon.domain.Coupon;
import kr.hhplus.be.server.coupon.domain.DiscountType;
import kr.hhplus.be.server.order.domain.Order;
import kr.hhplus.be.server.order.domain.OrderStatus;
import kr.hhplus.be.server.product.domain.Product;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    @Test
    void 유효한_상품과_쿠폰으로_주문을_생성하면_정상적으로_생성된다() {
        // Given
        List<Product> products = List.of(
                new Product(1L, "상품A", 1000L, 10L),
                new Product(2L, "상품B", 1500L, 5L)
        );
        Coupon coupon = new Coupon(1L, "COUPON1", DiscountType.FIXED, 500L, 10L, 1L);
        Long usedPoints = 200L;

        // When
        Order order = Order.create(1L, products, coupon, usedPoints);

        // Then
        assertEquals(2500L, order.getTotalPrice());
        assertEquals(1800L, order.getFinalPrice());
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void 유효한_상품만으로_주문을_생성하면_정상적으로_생성된다() {
        // Given
        List<Product> products = List.of(
                new Product(1L, "상품A", 1000L, 10L),
                new Product(2L, "상품B", 1500L, 5L)
        );
        Long usedPoints = 0L;

        // When
        Order order = Order.create(1L, products, null, usedPoints);

        // Then
        assertEquals(2500L, order.getTotalPrice());
        assertEquals(2500L, order.getFinalPrice());
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void 최종_결제_금액이_0보다_작으면_IllegalArgumentException_예외가_발생한다() {
        // Given
        List<Product> products = List.of(
                new Product(1L, "상품A", 1000L, 10L)
        );
        Coupon coupon = new Coupon(1L, "COUPON1", DiscountType.FIXED, 1500L, 10L, 1L); // 할인 금액이 상품 금액보다 큼
        Long usedPoints = 1000L;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> Order.create(1L, products, coupon, usedPoints));
    }

    @Test
    void 주문_상태를_결제_완료로_변경하면_정상적으로_변경된다() {
        // Given
        List<Product> products = List.of(
                new Product(1L, "상품A", 1000L, 10L)
        );
        Order order = Order.create(1L, products, null, 0L);

        // When
        order.markAsPaid();

        // Then
        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    void 결제_완료된_주문을_취소하면_IllegalStateException_예외가_발생한다() {
        // Given
        List<Product> products = List.of(
                new Product(1L, "상품A", 1000L, 10L)
        );
        Order order = Order.create(1L, products, null, 0L);
        order.markAsPaid();

        // When & Then
        assertThrows(IllegalStateException.class, order::cancelOrder);
    }

    @Test
    void 결제_대기중인_주문을_취소하면_정상적으로_취소된다() {
        // Given
        List<Product> products = List.of(
                new Product(1L, "상품A", 1000L, 10L)
        );
        Order order = Order.create(1L, products, null, 0L);

        // When
        order.cancelOrder();

        // Then
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void 결제_대기중인_주문을_결제_실패로_변경하면_정상적으로_변경된다() {
        // Given
        List<Product> products = List.of(
                new Product(1L, "상품A", 1000L, 10L)
        );
        Order order = Order.create(1L, products, null, 0L);

        // When
        order.markAsFailed();

        // Then
        assertEquals(OrderStatus.FAILED, order.getStatus());
    }
}