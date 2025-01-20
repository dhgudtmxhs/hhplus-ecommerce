package kr.hhplus.be.server.order;

import kr.hhplus.be.server.order.application.OrderItemData;
import kr.hhplus.be.server.order.domain.*;
import kr.hhplus.be.server.order.infra.OrderItemJpaRepository;
import kr.hhplus.be.server.order.infra.OrderJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private OrderItemJpaRepository orderItemJpaRepository;


    @BeforeEach
    public void setup() {
        orderJpaRepository.deleteAll();
    }

    @Test
    void 주문이_정상적으로_생성된다() {
        // Given
        Long userId = 1L;
        List<OrderItemData> orderItemDataList = List.of(
                new OrderItemData(1L, "상품A", 2L, 1000L),
                new OrderItemData(2L, "상품B", 1L, 1500L)
        );

        // When
        Order createdOrder = orderService.createOrder(userId, orderItemDataList);

        // Then
        assertNotNull(createdOrder);
        assertEquals(userId, createdOrder.getUserId(), "생성된 주문의 사용자 ID가 일치해야 함");
        assertEquals(3500L, createdOrder.getTotalPrice(), "생성된 주문의 총 금액이 정확해야 함");
        assertEquals(OrderStatus.CREATED, createdOrder.getStatus(), "생성된 주문의 상태가 CREATED여야 함");

        List<OrderItem> savedOrderItems = orderItemJpaRepository.findByOrderId(createdOrder.getId());
        assertEquals(2, savedOrderItems.size(), "저장된 주문 항목 개수가 2개여야 함");

        OrderItem item1 = savedOrderItems.get(0);
        assertEquals(1L, item1.getProductId(), "첫 번째 주문 항목의 상품 ID가 일치해야 함");
        assertEquals(2000L, item1.getPrice(), "첫 번째 주문 항목의 가격이 정확해야 함");

        OrderItem item2 = savedOrderItems.get(1);
        assertEquals(2L, item2.getProductId(), "두 번째 주문 항목의 상품 ID가 일치해야 함");
        assertEquals(1500L, item2.getPrice(), "두 번째 주문 항목의 가격이 정확해야 함");
    }
}