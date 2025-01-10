package kr.hhplus.be.server.product;

import kr.hhplus.be.server.order.application.ProductOrderCommand;
import kr.hhplus.be.server.product.domain.Product;
import kr.hhplus.be.server.product.domain.ProductRepository;
import kr.hhplus.be.server.product.domain.ProductService;
import kr.hhplus.be.server.product.infra.ProductEntity;
import kr.hhplus.be.server.product.infra.ProductJpaRepository;
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

    @BeforeEach
    void setUp() {
        List<ProductEntity> products = Arrays.asList(
                new ProductEntity(null, "상품A", 1000L, 10L),
                new ProductEntity(null, "상품B", 1500L, 5L),
                new ProductEntity(null, "상품C", 1200L, 8L),
                new ProductEntity(null, "상품D", 1300L, 12L),
                new ProductEntity(null, "상품E", 1100L, 15L),
                new ProductEntity(null, "상품F", 1400L, 7L)
        );
        productJpaRepository.saveAll(products);
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
    void 인기_상품_조회시_정상적으로_반환한다() {
        // Given
        List<Product> popularProducts = productService.getPopularProducts();

        // When
        List<Product> result = productService.getPopularProducts();

        // Then
        assertEquals(popularProducts.size(), result.size(), "인기 상품 개수가 일치해야 함");
    }

    @Test
    void 유효한_상품_목록으로_재고_차감시_정상적으로_수행된다() {
        // Given
        List<ProductOrderCommand> productOrders = Arrays.asList(
                new ProductOrderCommand(1L, 3L),
                new ProductOrderCommand(2L, 2L)
        );

        // When
        List<Product> result = productService.reduceStockAndGetProducts(productOrders);

        // Then
        assertEquals(2, result.size(), "재고 차감 후 반환된 상품 개수가 일치해야 함");
    }


}