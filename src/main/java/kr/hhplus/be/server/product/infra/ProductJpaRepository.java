package kr.hhplus.be.server.product.infra;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductEntity p WHERE p.id = :productId")
    Optional<Product> findByIdForUpdate(Long productId);
}
