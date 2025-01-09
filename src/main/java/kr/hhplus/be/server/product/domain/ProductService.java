package kr.hhplus.be.server.product.domain;

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
                            .orElseThrow(() -> new NoSuchElementException("상품 ID " + productOrder.productId() + "가 없습니다"));

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
                    .orElseThrow(() -> new NoSuchElementException("상품 ID " + productOrder.productId() + "가 없습니다"));

            // 차감된 수량만큼 재고 복원
            product = product.addStock(productOrder.quantity());

            productRepository.save(product);
        });
    }
}
