package kr.hhplus.be.server.product.domain;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> findProducts(int page, int size);

    long countTotalProducts();

    List<Product> findPopularProducts();

    Optional<Product> findByIdForUpdate(Long productId);

    void save(Product product);
}
