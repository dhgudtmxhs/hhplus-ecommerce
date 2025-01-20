package kr.hhplus.be.server.order;

import kr.hhplus.be.server.order.application.OrderItemData;
import kr.hhplus.be.server.order.domain.Order;
import kr.hhplus.be.server.order.domain.OrderItem;
import kr.hhplus.be.server.order.domain.OrderRepository;
import kr.hhplus.be.server.order.domain.OrderService;
import kr.hhplus.be.server.order.domain.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Long userId;
    private List<OrderItemData> orderItemDataList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Test 데이터 초기화
        userId = 1L;
        orderItemDataList = List.of(
                new OrderItemData(1L, "상품A", 2L, 1000L),
                new OrderItemData(2L, "상품B", 1L, 1500L)
        );
    }

    @Test
    void 주문이_정상적으로_생성되고_저장된다() {
        // Given
        Order expectedOrder = Order.builder()
                .id(null)
                .userId(userId)
                .totalPrice(3500L)
                .status(OrderStatus.CREATED)
                .build();

        when(orderRepository.saveOrder(any(Order.class))).thenReturn(expectedOrder);

        // When
        Order result = orderService.createOrder(userId, orderItemDataList);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(3500L, result.getTotalPrice());
        assertEquals(OrderStatus.CREATED, result.getStatus());

        verify(orderRepository, times(1)).saveOrder(any(Order.class));
        verify(orderRepository, times(orderItemDataList.size())).saveOrderItem(any(OrderItem.class));
    }

}