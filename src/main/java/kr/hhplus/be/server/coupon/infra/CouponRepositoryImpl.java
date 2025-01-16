package kr.hhplus.be.server.coupon.infra;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.coupon.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;
    private final UserCouponJpaRepository userCouponJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserCoupon> findByUserIdAndIsUsedFalse(Long userId) {
        return userCouponJpaRepository.findByUserIdAndIsUsedFalse(userId);
    }

    @Override
    public void saveCoupon(Coupon updatedCoupon) {
        couponJpaRepository.save(updatedCoupon);
    }

    @Override
    public UserCoupon saveUserCoupon(UserCoupon userCoupon) {
        return userCouponJpaRepository.save(userCoupon);
    }

    @Override
    public Optional<Coupon> findByUserIdAndCouponIdAndIsUsedFalse(Long userId, Long couponId) {
        QCoupon coupon = QCoupon.coupon;
        QUserCoupon userCoupon = QUserCoupon.userCoupon;

        Coupon result = queryFactory
                .select(coupon)
                .from(coupon)
                .join(userCoupon).on(userCoupon.couponId.eq(coupon.id))
                .where(userCoupon.userId.eq(userId)
                        .and(userCoupon.couponId.eq(couponId))
                        .and(userCoupon.isUsed.isFalse()))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<UserCoupon> findByUserIdAndCouponIdAndIsUsedFalseForUpdate(Long userId, Long couponId) {
        return userCouponJpaRepository.findByUserIdAndCouponIdAndIsUsedFalseForUpdate(userId, couponId);
    }


    @Override
    public Optional<Coupon> findByIdForUpdate(Long couponId) {
        return couponJpaRepository.findByIdForUpdate(couponId);
    }

    @Override
    public Optional<UserCoupon> findByCouponIdAndUserIdForUpdate(Long couponId, Long userId) {
        return userCouponJpaRepository.findByCouponIdAndUserIdForUpdate(couponId, userId);
    }

}