package kr.hhplus.be.server.coupon.infra;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import kr.hhplus.be.server.coupon.domain.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long> {

    List<UserCoupon> findByUserIdAndIsUsedFalse(Long userId);

    @Query("SELECT uc FROM UserCoupon uc WHERE uc.userId = :userId AND uc.couponId = :couponId AND uc.isUsed = false")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<UserCoupon> findByUserIdAndCouponIdAndIsUsedFalseForUpdate(@Param("userId") Long userId,
                                                                        @Param("couponId") Long couponId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "1000")})
    @Query("SELECT uc FROM UserCoupon uc WHERE uc.couponId = :couponId AND uc.userId = :userId")
    Optional<UserCoupon> findByCouponIdAndUserIdForUpdate(Long couponId, Long userId);
}
