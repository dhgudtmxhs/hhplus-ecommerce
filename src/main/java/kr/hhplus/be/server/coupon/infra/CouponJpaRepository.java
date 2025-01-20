package kr.hhplus.be.server.coupon.infra;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.coupon.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {

    @Query("SELECT c FROM Coupon c WHERE c.id = :couponId")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Coupon> findByIdForUpdate(@Param("couponId") Long couponId);
}
