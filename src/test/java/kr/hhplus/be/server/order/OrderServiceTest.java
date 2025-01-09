package kr.hhplus.be.server.order;

import kr.hhplus.be.server.coupon.domain.Coupon;
import kr.hhplus.be.server.order.domain.Order;
import kr.hhplus.be.server.order.domain.OrderRepository;
import kr.hhplus.be.server.order.domain.OrderService;
import kr.hhplus.be.server.point.domain.Point;
import kr.hhplus.be.server.product.domain.Product;
import kr.hhplus.be.server.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 유효한_정보로_주문을_생성하면_정상적으로_저장된다() {
        // Given
        User user = new User(1L, "사용자A");
        List<Product> products = List.of(
                new Product(1L, "상품A", 1000L, 10L),
                new Product(2L, "상품B", 1500L, 5L)
        );
        Coupon coupon = new Coupon(1L, "COUPON1", kr.hhplus.be.server.coupon.domain.DiscountType.FIXED, 500L, 10L, 1L);
        Point point = new Point(1L, user.id(), 200L);

        Order expectedOrder = Order.create(user.id(), products, coupon, point.point());
        when(orderRepository.save(any(Order.class))).thenReturn(expectedOrder);

        // When
        Order result = orderService.createOrder(user, products, coupon, point);

        // Then
        assertEquals(expectedOrder, result);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void 결제_성공시_주문_상태가_결제_완료로_변경된다() {
        // Given
        Order order = mock(Order.class);
        doNothing().when(order).markAsPaid();

        // When
        orderService.updateOrderStatus(order, true);

        // Then
        verify(order).markAsPaid();
        verify(orderRepository).save(order);
    }

    @Test
    void 결제_실패시_주문_상태가_결제_실패로_변경된다() {
        // Given
        Order order = mock(Order.class);
        doNothing().when(order).markAsFailed();

        // When
        orderService.updateOrderStatus(order, false);

        // Then
        verify(order).markAsFailed();
        verify(orderRepository).save(order);
    }
}