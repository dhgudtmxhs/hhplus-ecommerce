package kr.hhplus.be.server.product.domain;

import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.order.application.ProductOrderCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getProducts(int page, int size) {
        return productRepository.findProducts(page, size);
    }

    public long getTotalCount() {
        return productRepository.countTotalProducts();
    }

    public List<Product> getPopularProducts() {
        return productRepository.findPopularProducts();
    }

    public List<Product> findByIdForUpdate(List<Long> productIds) {
        List<Product> products = productIds.stream()
                .map(productId -> productRepository.findByIdForUpdate(productId)
                        .orElseThrow(() -> new NoSuchElementException(ErrorCode.PRODUCT_NOT_FOUND_CODE)))
                .toList();

        if (products.size() != productIds.size()) {
            throw new NoSuchElementException(ErrorCode.PRODUCT_NOT_FOUND_CODE);
        }

        return products;
    }

    public void deductStock(Map<Long, Long> productQuantities) {
        productQuantities.forEach((productId, quantity) -> {
            Product product = productRepository.findByIdForUpdate(productId)
                    .orElseThrow(() -> new NoSuchElementException(ErrorCode.PRODUCT_NOT_FOUND_CODE));
            product.reduceStock(quantity);
            productRepository.save(product);
        });
    }
}
