package kr.hhplus.be.server.coupon.infra;

import aj.org.objectweb.asm.commons.Remapper;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserCouponJpaRepository extends JpaRepository<UserCouponEntity, Long> {

    List<UserCouponEntity> findByUserIdAndIsUsedFalse(Long userId);

    @Query("SELECT uc FROM UserCouponEntity uc WHERE uc.user.id = :userId AND uc.coupon.id = :couponId AND uc.isUsed = false")
    @Lock(LockModeType.PESSIMISTIC_WRITE) // 비관적 락 설정
    Optional<UserCouponEntity> findByUserIdAndCouponIdAndIsUsedFalseForUpdate(@Param("userId") Long userId,
                                                                              @Param("couponId") Long couponId);
}
