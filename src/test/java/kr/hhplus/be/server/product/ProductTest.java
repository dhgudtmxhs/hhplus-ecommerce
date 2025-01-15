package kr.hhplus.be.server.product;

import kr.hhplus.be.server.product.domain.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

    @Test
    void 유효한_재고_차감은_정상적으로_수행된다() {
        // Given
        Product product = new Product(1L, "상품A", 1000L, 10L);
        Long quantityToReduce = 5L;

        // When
        product.reduceStock(quantityToReduce);

        // Then
        assertEquals(5L, product.getStock(), "재고가 정상적으로 차감되어야 함");
    }

    @Test
    void 재고보다_많은_수량을_차감하면_IllegalArgumentException_예외가_발생한다() {
        // Given
        Product product = new Product(1L, "상품A", 1000L, 5L);
        Long quantityToReduce = 10L;

        // When && Then
        assertThrows(IllegalArgumentException.class, () -> product.reduceStock(quantityToReduce));
    }

    @Test
    void 유효한_재고_추가는_정상적으로_수행된다() {
        // Given
        Product product = new Product(1L, "상품A", 1000L, 5L);
        Long quantityToAdd = 10L;

        // When
        product.addStock(quantityToAdd);

        // Then
        assertEquals(15L, product.getStock(), "재고가 정상적으로 추가되어야 함");
    }

    @Test
    void 음수_또는_0_수량으로_재고를_추가하면_IllegalArgumentException_예외가_발생한다() {
        // Given
        Product product = new Product(1L, "상품A", 1000L, 5L);

        // When && Then
        assertThrows(IllegalArgumentException.class, () -> product.addStock(0L));
        assertThrows(IllegalArgumentException.class, () -> product.addStock(-5L));
    }

    @Test
    void 음수_또는_0_수량으로_재고를_차감하면_IllegalArgumentException_예외가_발생한다() {
        // Given
        Product product = new Product(1L, "상품A", 1000L, 10L);

        // When && Then
        assertThrows(IllegalArgumentException.class, () -> product.reduceStock(0L));
        assertThrows(IllegalArgumentException.class, () -> product.reduceStock(-5L));
    }

    @Test
    void 유효하지_않은_가격이나_재고로_상품_생성시_IllegalArgumentException_예외가_발생한다() {
        // When && Then
        assertThrows(IllegalArgumentException.class, () -> new Product(1L, "상품A", 0L, 10L));  // 가격이 0인 경우
        assertThrows(IllegalArgumentException.class, () -> new Product(1L, "상품A", 1000L, -1L)); // 재고가 음수인 경우
    }
}