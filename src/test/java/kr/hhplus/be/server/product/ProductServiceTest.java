package kr.hhplus.be.server.product;

import kr.hhplus.be.server.order.application.ProductOrderCommand;
import kr.hhplus.be.server.product.domain.Product;
import kr.hhplus.be.server.product.domain.ProductRepository;
import kr.hhplus.be.server.product.domain.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 페이지와_사이즈를_지정하여_상품목록을_조회하면_정상적으로_반환한다() {
        // Given
        int page = 0;
        int size = 5;
        List<Product> mockProducts = Arrays.asList(
                new Product(1L, "상품A", 1000L, 10L),
                new Product(2L, "상품B", 1500L, 5L)
        );
        when(productRepository.findProducts(page, size)).thenReturn(mockProducts);

        // When
        List<Product> result = productService.getProducts(page, size);

        // Then
        assertEquals(2, result.size());
        assertEquals("상품A", result.get(0).getName());
        verify(productRepository).findProducts(page, size);
    }

    @Test
    void 총_상품_개수를_조회하면_정상적으로_반환한다() {
        // Given
        long totalCount = 10L;
        when(productRepository.countTotalProducts()).thenReturn(totalCount);

        // When
        long result = productService.getTotalCount();

        // Then
        assertEquals(10L, result);
        verify(productRepository).countTotalProducts();
    }

    @Test
    void 인기_상품_5개를_조회시_정상적으로_반환한다() {
        // Given
        List<Product> mockPopularProducts = Arrays.asList(
                new Product(1L, "Popular1", 1000L, 100L),
                new Product(2L, "Popular2", 1500L, 90L),
                new Product(3L, "Popular3", 1200L, 80L),
                new Product(4L, "Popular4", 2000L, 70L),
                new Product(5L, "Popular5", 3000L, 60L)
        );
        when(productRepository.findPopularProducts()).thenReturn(mockPopularProducts);

        // When
        List<Product> result = productService.getPopularProducts();

        // Then
        assertEquals(5, result.size());
        assertEquals("Popular1", result.get(0).getName());
        verify(productRepository).findPopularProducts();
    }

    @Test
    void 인기_상품_조회_시_없을_경우_빈_리스트를_반환한다() {
        // Given
        when(productRepository.findPopularProducts()).thenReturn(Collections.emptyList());

        // When
        List<Product> result = productService.getPopularProducts();

        // Then
        assertTrue(result.isEmpty());
        verify(productRepository).findPopularProducts();
    }




}
