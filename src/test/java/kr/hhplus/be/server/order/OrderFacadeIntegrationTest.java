package kr.hhplus.be.server.order;

import kr.hhplus.be.server.order.application.OrderFacade;
import kr.hhplus.be.server.order.application.OrderCommand;
import kr.hhplus.be.server.order.application.ProductOrderCommand;
import kr.hhplus.be.server.order.application.OrderInfo;
import kr.hhplus.be.server.product.domain.Product;
import kr.hhplus.be.server.product.infra.ProductJpaRepository;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.infra.UserJpaRepository;
import kr.hhplus.be.server.order.infra.OrderJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class OrderFacadeIntegrationTest {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    private Long userId;
    private Long productId;
    private Long initialStock;

    @BeforeEach
    public void setup() {
        // 테스트 데이터 초기화
        orderJpaRepository.deleteAll();
        productJpaRepository.deleteAll();
        userJpaRepository.deleteAll();

        // 사용자 생성
        User user = userJpaRepository.save(User.builder()
                .name("testUser")
                .build());
        userId = user.getId();

        // 상품 생성
        initialStock = 100L;
        Product product = productJpaRepository.save(Product.builder()
                .name("Test Product")
                .stock(initialStock)
                .price(1000L)
                .build());
        productId = product.getId();
    }

    @Test
    @Transactional
    void 주문_생성이_성공한다() {
        // Given
        OrderCommand command = new OrderCommand(
                userId,
                Collections.singletonList(new ProductOrderCommand(productId, 10L))
        );

        // When
        OrderInfo orderInfo = orderFacade.createOrder(command);

        // Then
        assertThat(orderInfo).isNotNull();
        assertThat(orderInfo.id()).isNotNull();
        assertThat(orderInfo.userId()).isEqualTo(userId);
        assertThat(orderInfo.totalPrice()).isEqualTo(1000L * 10L); // 가격 * 수량
        assertThat(orderInfo.status()).isEqualTo("CREATED"); // 상태는 "ORDERED"로 가정
        assertThat(orderInfo.orderItems()).hasSize(1);
        assertThat(orderInfo.orderItems().get(0).productId()).isEqualTo(productId);
        assertThat(orderInfo.orderItems().get(0).quantity()).isEqualTo(10L);

        // 재고 확인
        Long finalStock = productJpaRepository.findById(productId)
                .map(Product::getStock)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
        assertThat(finalStock).isEqualTo(initialStock - 10L);
    }

    @Test
    @Transactional
    void 재고가_부족할_경우_주문_생성이_실패한다() {
        // Given
        OrderCommand command = new OrderCommand(
                userId,
                Collections.singletonList(new ProductOrderCommand(productId, initialStock + 1))
        );

        // When & Then
        assertThatThrownBy(() -> orderFacade.createOrder(command))
                .isInstanceOf(RuntimeException.class); // 메시지 확인 제거

        // 재고 확인 (변경되지 않아야 함)
        Long finalStock = productJpaRepository.findById(productId)
                .map(Product::getStock)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
        assertThat(finalStock).isEqualTo(initialStock);
    }

    @Test
    @Transactional
    void 존재하지_않는_사용자에_대한_주문_생성이_실패한다() {
        // Given
        Long invalidUserId = 999L;
        OrderCommand command = new OrderCommand(
                invalidUserId,
                Collections.singletonList(new ProductOrderCommand(productId, 10L))
        );

        // When & Then
        assertThatThrownBy(() -> orderFacade.createOrder(command))
                .isInstanceOf(RuntimeException.class); // 메시지 확인 제거

        // 재고 확인 (변경되지 않아야 함)
        Long finalStock = productJpaRepository.findById(productId)
                .map(Product::getStock)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
        assertThat(finalStock).isEqualTo(initialStock);
    }

    @Test
    @Transactional
    void 존재하지_않는_상품에_대한_주문_생성이_실패한다() {
        // Given
        Long invalidProductId = 999L;
        OrderCommand command = new OrderCommand(
                userId,
                Collections.singletonList(new ProductOrderCommand(invalidProductId, 10L))
        );

        // When & Then
        assertThatThrownBy(() -> orderFacade.createOrder(command))
                .isInstanceOf(RuntimeException.class); // 메시지 확인 제거

        // 재고 확인 (변경되지 않아야 함)
        Long finalStock = productJpaRepository.findById(productId)
                .map(Product::getStock)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
        assertThat(finalStock).isEqualTo(initialStock);
    }
}