package kr.hhplus.be.server.product;

import kr.hhplus.be.server.order.application.ProductOrderCommand;
import kr.hhplus.be.server.order.domain.Order;
import kr.hhplus.be.server.order.domain.OrderItem;
import kr.hhplus.be.server.order.domain.OrderStatus;
import kr.hhplus.be.server.order.infra.OrderItemRepository;
import kr.hhplus.be.server.order.infra.OrderJpaRepository;
import kr.hhplus.be.server.product.domain.Product;
import kr.hhplus.be.server.product.domain.ProductService;
import kr.hhplus.be.server.product.infra.ProductJpaRepository;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.infra.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
        Product productA = productJpaRepository.save(new Product(null, "상품A", 1000L, 100L));
        Product productB = productJpaRepository.save(new Product(null, "상품B", 1500L, 50L));
        Product productC = productJpaRepository.save(new Product(null, "상품C", 1200L, 8L));
        Product productD = productJpaRepository.save(new Product(null, "상품D", 1300L, 12L));
        Product productE = productJpaRepository.save(new Product(null, "상품E", 1100L, 15L));
        Product productF = productJpaRepository.save(new Product(null, "상품F", 1400L, 7L));

        // 사용자 ID
        Long userId = 1L;

        // 주문 저장
        Order order = orderJpaRepository.save(Order.builder()
                .userId(userId)
                .totalPrice(10000L)
                .finalPrice(9500L)
                .status(OrderStatus.PENDING)
                .build());

        // 주문 항목 개별 저장
        orderItemJpaRepository.save(new OrderItem(order.getId(), productA.getId(), 1000L, 15L));
        orderItemJpaRepository.save(new OrderItem(order.getId(), productB.getId(), 1500L, 10L));
        orderItemJpaRepository.save(new OrderItem(order.getId(), productC.getId(), 1200L, 8L));
        orderItemJpaRepository.save(new OrderItem(order.getId(), productD.getId(), 1300L, 5L));
        orderItemJpaRepository.save(new OrderItem(order.getId(), productE.getId(), 1100L, 20L));
        orderItemJpaRepository.save(new OrderItem(order.getId(), productF.getId(), 1400L, 7L));

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
        assertEquals("상품A", result.get(0).getName());
        assertEquals("상품B", result.get(1).getName());
        assertEquals("상품C", result.get(2).getName());
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
        assertEquals("상품D", result.get(0).getName());
        assertEquals("상품E", result.get(1).getName());
        assertEquals("상품F", result.get(2).getName());
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
        Product product1 = productJpaRepository.findById(productOrders.get(0).productId()).orElseThrow();
        Product product2 = productJpaRepository.findById(productOrders.get(1).productId()).orElseThrow();

        assertEquals(97L, product1.getStock(), "상품 1의 재고가 3개 차감되어 97이어야 함");
        assertEquals(48L, product2.getStock(), "상품 2의 재고가 2개 차감되어 48이어야 함");
    }



}