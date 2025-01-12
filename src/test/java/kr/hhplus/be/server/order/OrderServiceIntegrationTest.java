package kr.hhplus.be.server.order;

import kr.hhplus.be.server.coupon.domain.Coupon;
import kr.hhplus.be.server.coupon.domain.DiscountType;
import kr.hhplus.be.server.order.application.ProductOrderCommand;
import kr.hhplus.be.server.order.domain.*;
import kr.hhplus.be.server.point.domain.Point;
import kr.hhplus.be.server.product.domain.Product;
import kr.hhplus.be.server.user.domain.User;
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
    private OrderRepository orderRepository;

    private User user;
    private List<Product> products;
    private Coupon coupon;
    private Point point;
    private List<ProductOrderCommand> productOrderCommands;

    @BeforeEach
    void setUp() {
        user = new User(1L, "사용자A");
        products = List.of(
                new Product(1L, "상품A", 100000L, 10L),
                new Product(2L, "상품B", 1500L, 5L)
        );
        coupon = new Coupon(1L, "COUPON1", DiscountType.FIXED, 500L, 10L, 1L);
        point = new Point(1L, user.id(), 200L);

        // ProductOrderCommand 리스트 생성 (productId와 수량 지정)
        productOrderCommands = List.of(
                new ProductOrderCommand(1L, 2L), // 상품A 2개 주문
                new ProductOrderCommand(2L, 1L)  // 상품B 1개 주문
        );
    }

    @Test
    void 유효한_정보로_주문을_생성하면_정상적으로_저장된다() {
        // When
        Order result = orderService.createOrder(user, products, coupon, point, productOrderCommands);

        // Then
        assertNotNull(result);
        assertEquals(user.id(), result.getUserId());
        assertEquals(2, result.getOrderItems().size());
        assertEquals(201500L, result.getTotalPrice()); // 총 금액 = 1000*2 + 1500*1

        // 상품A에 대한 수량 검증
        OrderItem itemA = result.getOrderItems().stream()
                .filter(item -> item.productId().equals(1L))
                .findFirst()
                .orElseThrow();
        assertEquals(2L, itemA.quantity()); // 상품A 수량 검증

        // 상품B에 대한 수량 검증
        OrderItem itemB = result.getOrderItems().stream()
                .filter(item -> item.productId().equals(2L))
                .findFirst()
                .orElseThrow();
        assertEquals(1L, itemB.quantity()); // 상품B 수량 검증
    }

    @Test
    void 결제_성공시_주문_상태가_결제_완료로_변경된다() {
        // Given
        Order order = orderService.createOrder(user, products, coupon, point, productOrderCommands);

        // When
        orderService.updateOrderStatus(order, true);

        // Then
        assertEquals(OrderStatus.PAID, order.getStatus());
    }
}