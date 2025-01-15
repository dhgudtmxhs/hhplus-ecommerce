package kr.hhplus.be.server.product.domain;

import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.order.application.ProductOrderCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    @Transactional
    public List<Product> reduceStockAndGetProducts(List<ProductOrderCommand> productOrders) {
        return productOrders.stream()
                .map(productOrder -> {

                    Product product = productRepository.findByIdForUpdate(productOrder.productId())
                            .orElseThrow(() -> new NoSuchElementException(ErrorCode.PRODUCT_NOT_FOUND_CODE));

                    // 재고 차감 후 새로운 도메인 객체 생성
                    product.reduceStock(productOrder.quantity());

                    productRepository.save(product);

                    return product;
                })
                .collect(Collectors.toList());
    }


    @Transactional
    public void restoreStock(List<ProductOrderCommand> products) {
        products.forEach(productOrder -> {
            Product product = productRepository.findByIdForUpdate(productOrder.productId())
                    .orElseThrow(() -> new NoSuchElementException(ErrorCode.PRODUCT_NOT_FOUND_CODE));

            // 차감된 수량만큼 재고 복원
            product.addStock(productOrder.quantity());

            productRepository.save(product);
        });
    }
}
