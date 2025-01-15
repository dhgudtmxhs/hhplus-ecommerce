package kr.hhplus.be.server.product.infra;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.order.domain.QOrderItem;
import kr.hhplus.be.server.product.domain.Product;
import kr.hhplus.be.server.product.domain.ProductRepository;
import kr.hhplus.be.server.product.domain.QProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Product> findProducts(int page, int size) {
        int offset = page * size;

        QProduct product = QProduct.product;
        return queryFactory
                .selectFrom(product)
                .orderBy(product.id.asc())
                .offset(offset)
                .limit(size)
                .fetch();
    }

    @Override
    public long countTotalProducts() {
        return productJpaRepository.count();
    }

    @Override
    public List<Product> findPopularProducts() {
        QProduct product = QProduct.product;
        QOrderItem orderItem = QOrderItem.orderItem;

        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);

        return queryFactory
                .selectFrom(product)
                .innerJoin(orderItem).on(orderItem.productId.eq(product.id)
                        .and(orderItem.createdAt.goe(threeDaysAgo)))
                .groupBy(product.id)
                .orderBy(orderItem.quantity.sum().desc())
                .limit(5)
                .fetch();
    }

    @Override
    public Optional<Product> findByIdForUpdate(Long productId) {
        return productJpaRepository.findByIdForUpdate(productId);
    }

    @Override
    public void save(Product product) {
        productJpaRepository.save(product);
    }
}