package kr.hhplus.be.server.product;

import kr.hhplus.be.server.order.application.ProductOrderCommand;
import kr.hhplus.be.server.order.domain.Order;
import kr.hhplus.be.server.order.domain.OrderItem;
import kr.hhplus.be.server.order.domain.OrderStatus;
import kr.hhplus.be.server.order.infra.OrderItemJpaRepository;
import kr.hhplus.be.server.order.infra.OrderJpaRepository;
import kr.hhplus.be.server.product.domain.Product;
import kr.hhplus.be.server.product.domain.ProductService;
import kr.hhplus.be.server.product.infra.ProductJpaRepository;
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
    private OrderItemJpaRepository orderItemJpaRepository;

    private List<ProductOrderCommand> productOrders;

    @BeforeEach
    void setUp() {
        orderItemJpaRepository.deleteAll();
        productJpaRepository.deleteAll();

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
                .status(OrderStatus.CREATED)
                .build());

        // 주문 항목 개별 저장
        orderItemJpaRepository.save(new OrderItem(null, order.getId(), productA.getId(), "1", 1000L, 15L));
        orderItemJpaRepository.save(new OrderItem(null, order.getId(), productB.getId(), "2",1500L, 10L));
        orderItemJpaRepository.save(new OrderItem(null, order.getId(), productC.getId(), "3",1200L, 8L));
        orderItemJpaRepository.save(new OrderItem(null, order.getId(), productD.getId(), "4",1300L, 5L));
        orderItemJpaRepository.save(new OrderItem(null, order.getId(), productE.getId(), "5",1100L, 20L));
        orderItemJpaRepository.save(new OrderItem(null, order.getId(), productF.getId(), "6",1400L, 7L));

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
        // 인기상품 날짜 상세 테스트는 ProductControllerIntegrationTest
    }



}