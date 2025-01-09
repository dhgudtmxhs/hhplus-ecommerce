package kr.hhplus.be.server.product.infra;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.order.infra.QOrderItemEntity;
import kr.hhplus.be.server.product.domain.Product;
import kr.hhplus.be.server.product.domain.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final ProductMapper productMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Product> findProducts(int page, int size) {
        int offset = page * size;

        QProductEntity product = QProductEntity.productEntity;
        List<ProductEntity> productEntities = queryFactory
                .selectFrom(product)
                .orderBy(product.id.asc())
                .offset(offset)
                .limit(size)
                .fetch();

        return productMapper.toDomainList(productEntities);
    }

    @Override
    public long countTotalProducts() {
        return productJpaRepository.count();
    }

    @Override
    public List<Product> findPopularProducts() {
        QProductEntity product = QProductEntity.productEntity;
        QOrderItemEntity orderItem = QOrderItemEntity.orderItemEntity;

        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);

        List<ProductEntity> popularEntities = queryFactory
                .selectFrom(product)
                .innerJoin(orderItem).on(orderItem.product.id.eq(product.id)
                        .and(orderItem.createdAt.goe(threeDaysAgo)))
                .groupBy(product.id)
                .orderBy(orderItem.quantity.sum().desc())
                .limit(5)
                .fetch();

        return productMapper.toDomainList(popularEntities);
    }

    @Override
    public Optional<Product> findByIdForUpdate(Long productId) {
        return productJpaRepository.findByIdForUpdate(productId);
    }

    @Override
    public void save(Product product) {
        ProductEntity productEntity = productMapper.toEntity(product);
        productJpaRepository.save(productEntity);
    }


}
