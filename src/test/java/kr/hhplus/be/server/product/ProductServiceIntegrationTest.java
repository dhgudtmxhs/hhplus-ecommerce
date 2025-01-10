package kr.hhplus.be.server.product;

import kr.hhplus.be.server.order.application.ProductOrderCommand;
import kr.hhplus.be.server.order.domain.OrderStatus;
import kr.hhplus.be.server.order.infra.OrderEntity;
import kr.hhplus.be.server.order.infra.OrderItemEntity;
import kr.hhplus.be.server.order.infra.OrderItemRepository;
import kr.hhplus.be.server.order.infra.OrderJpaRepository;
import kr.hhplus.be.server.product.domain.Product;
import kr.hhplus.be.server.product.domain.ProductRepository;
import kr.hhplus.be.server.product.domain.ProductService;
import kr.hhplus.be.server.product.infra.ProductEntity;
import kr.hhplus.be.server.product.infra.ProductJpaRepository;
import kr.hhplus.be.server.user.infra.UserEntity;
import kr.hhplus.be.server.user.infra.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private OrderItemRepository orderItemJpaRepository;

    private List<ProductOrderCommand> productOrders;

    @BeforeEach
    void setUp() {
        // 상품 개별 저장 후 반환된 실제 엔티티 사용
        ProductEntity productA = productJpaRepository.save(new ProductEntity(null, "상품A", 1000L, 100L));
        ProductEntity productB = productJpaRepository.save(new ProductEntity(null, "상품B", 1500L, 50L));
        ProductEntity productC = productJpaRepository.save(new ProductEntity(null, "상품C", 1200L, 8L));
        ProductEntity productD = productJpaRepository.save(new ProductEntity(null, "상품D", 1300L, 12L));
        ProductEntity productE = productJpaRepository.save(new ProductEntity(null, "상품E", 1100L, 15L));
        ProductEntity productF = productJpaRepository.save(new ProductEntity(null, "상품F", 1400L, 7L));

        // 사용자 저장
        UserEntity user = userJpaRepository.save(new UserEntity(null, "사용자A"));

        // 주문 저장
        OrderEntity order = orderJpaRepository.save(OrderEntity.builder()
                .user(user)
                .totalPrice(10000L)
                .finalPrice(9500L)
                .status(OrderStatus.PENDING)
                .build());

        // 주문 항목 개별 저장
        orderItemJpaRepository.save(new OrderItemEntity(null, productA, 15L, 1000L, order));
        orderItemJpaRepository.save(new OrderItemEntity(null, productB, 10L, 1500L, order));
        orderItemJpaRepository.save(new OrderItemEntity(null, productC, 8L, 1200L, order));
        orderItemJpaRepository.save(new OrderItemEntity(null, productD, 5L, 1300L, order));
        orderItemJpaRepository.save(new OrderItemEntity(null, productE, 20L, 1100L, order));
        orderItemJpaRepository.save(new OrderItemEntity(null, productF, 7L, 1400L, order));

        // 재고 차감 테스트
        productOrders = Arrays.asList(
                new ProductOrderCommand(productA.getId(), 3L),
                new ProductOrderCommand(productB.getId(), 2L)
        );

    }

    @Test
    void 페이지와_사이즈를_지정하여_상품목록을_조회하면_정상적으로_반환한다() {
        // Given
        int page = 0;
        int size = 3;

        // When
        List<Product> result = productService.getProducts(page, size);

        // Then
        assertEquals(3, result.size(), "첫 페이지에 3개가 반환되어야 함");
        assertEquals("상품A", result.get(0).name());
        assertEquals("상품B", result.get(1).name());
        assertEquals("상품C", result.get(2).name());
    }

    @Test
    void 두번째_페이지_상품목록_조회시_정상적으로_반환한다() {
        // Given
        int page = 1;
        int size = 3;

        // When
        List<Product> result = productService.getProducts(page, size);

        // Then
        assertEquals(3, result.size(), "두 번째 페이지에 3개가 반환되어야 함");
        assertEquals("상품D", result.get(0).name());
        assertEquals("상품E", result.get(1).name());
        assertEquals("상품F", result.get(2).name());
    }

    @Test
    void 세번째_페이지_상품목록_조회시_빈_리스트를_반환한다() {
        // Given
        int page = 2;
        int size = 3;

        // When
        List<Product> result = productService.getProducts(page, size);

        // Then
        assertTrue(result.isEmpty(), "세 번째 페이지는 데이터가 없으므로 빈 리스트");
    }

    @Test
    void 총_상품_개수를_조회하면_정상적으로_반환한다() {
        // Given
        long totalCount = productJpaRepository.count();

        // When
        long result = productService.getTotalCount();

        // Then
        assertEquals(totalCount, result, "총 상품 개수가 일치해야 함");
    }

    @Test
    void 인기_상품_조회시_5개로_제한되어_반환한다() {
        // Given
        // When
        List<Product> result = productService.getPopularProducts();

        // Then
        assertEquals(5, result.size(), "인기 상품은 최대 5개만 반환되어야 함");
    }

    @Test
    void 유효한_상품_목록으로_재고_차감시_정상적으로_수행된다() {
        // Given: setUp() 메서드에서 productOrders가 준비됨

        // When
        List<Product> result = productService.reduceStockAndGetProducts(productOrders);

        // Then
        assertEquals(2, result.size(), "재고 차감 후 반환된 상품 개수가 일치해야 함");

        // 재고 차감 검증
        ProductEntity product1 = productJpaRepository.findById(productOrders.get(0).productId()).orElseThrow();
        ProductEntity product2 = productJpaRepository.findById(productOrders.get(1).productId()).orElseThrow();

        assertEquals(97L, product1.getStock(), "상품 1의 재고가 3개 차감되어 97이어야 함");
        assertEquals(48L, product2.getStock(), "상품 2의 재고가 2개 차감되어 48이어야 함");
    }



}