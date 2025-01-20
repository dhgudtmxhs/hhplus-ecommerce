package kr.hhplus.be.server.order;

import kr.hhplus.be.server.order.domain.Order;
import kr.hhplus.be.server.order.domain.OrderItem;
import kr.hhplus.be.server.order.domain.OrderStatus;
import kr.hhplus.be.server.common.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    @Test
    void 주문을_정상적으로_생성한다() {
        // Given
        List<OrderItem> orderItems = List.of(
                OrderItem.builder()
                        .productId(1L)
                        .productName("상품A")
                        .price(1000L)
                        .quantity(2L)
                        .build(),
                OrderItem.builder()
                        .productId(2L)
                        .productName("상품B")
                        .price(1500L)
                        .quantity(1L)
                        .build()
        );

        // When
        Order order = Order.builder()
                .userId(1L)
                .totalPrice(3500L)
                .status(OrderStatus.CREATED)
                .orderItems(orderItems)
                .build();

        // Then
        assertEquals(1L, order.getUserId());
        assertEquals(3500L, order.getTotalPrice());
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertNotNull(order.getOrderItems());
        assertEquals(2, order.getOrderItems().size());

        for (OrderItem item : order.getOrderItems()) {
            assertNull(item.getOrderId());
            assertNotNull(item.getProductId());
            assertNotNull(item.getProductName());
            assertTrue(item.getPrice() > 0);
            assertTrue(item.getQuantity() > 0);
        }
    }

    @Test
    void Order_create_메서드를_사용하여_정상적으로_생성된다() {
        // Given
        List<OrderItem> orderItems = List.of(
                OrderItem.builder()
                        .productId(1L)
                        .productName("상품A")
                        .price(1000L)
                        .quantity(2L)
                        .build(),
                OrderItem.builder()
                        .productId(2L)
                        .productName("상품B")
                        .price(1500L)
                        .quantity(1L)
                        .build()
        );

        // When
        Order order = Order.create(1L, orderItems);

        // Then
        assertEquals(1L, order.getUserId());
        assertEquals(3500L, order.getTotalPrice());
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertNotNull(order.getOrderItems());
        assertEquals(2, order.getOrderItems().size());

        for (OrderItem item : order.getOrderItems()) {
            assertNull(item.getOrderId());
            assertNotNull(item.getProductId());
            assertNotNull(item.getProductName());
            assertTrue(item.getPrice() > 0);
            assertTrue(item.getQuantity() > 0);
        }
    }

    @Test
    void markAsCompleted을_호출하면_상태가_COMPLETED로_변경된다() {
        // Given
        List<OrderItem> orderItems = List.of(
                OrderItem.builder()
                        .productId(1L)
                        .productName("상품A")
                        .price(1000L)
                        .quantity(1L)
                        .build()
        );
        Order order = Order.create(1L, orderItems);

        // When
        order.markAsCompleted();

        // Then
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void markAsCompleted을_호출했을_때_상태가_CREATED가_아니면_예외가_발생한다() {
        // Given
        List<OrderItem> orderItems = List.of(
                OrderItem.builder()
                        .productId(1L)
                        .productName("상품A")
                        .price(1000L)
                        .quantity(1L)
                        .build()
        );
        Order order = Order.create(1L, orderItems);
        order.markAsCompleted();

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, order::markAsCompleted);
        assertEquals(ErrorCode.ORDER_STATUS_CHANGE_INVALID_CODE, exception.getMessage());
    }

    @Test
    void markAsCancelled을_호출하면_상태가_CANCELLED로_변경된다() {
        // Given
        List<OrderItem> orderItems = List.of(
                OrderItem.builder()
                        .productId(1L)
                        .productName("상품A")
                        .price(1000L)
                        .quantity(1L)
                        .build()
        );
        Order order = Order.create(1L, orderItems);

        // When
        order.markAsCancelled();

        // Then
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void markAsCancelled을_호출했을_때_상태가_COMPLETED이면_예외가_발생한다() {
        // Given
        List<OrderItem> orderItems = List.of(
                OrderItem.builder()
                        .productId(1L)
                        .productName("상품A")
                        .price(1000L)
                        .quantity(1L)
                        .build()
        );
        Order order = Order.create(1L, orderItems);
        order.markAsCompleted();

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, order::markAsCancelled);
        assertEquals(ErrorCode.ORDER_CANCEL_INVALID_CODE, exception.getMessage());
    }
}