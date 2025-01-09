package kr.hhplus.be.server.coupon.domain;

import java.util.List;
import java.util.Optional;

public interface CouponRepository {
    List<UserCoupon> findByUserIdAndIsUsedFalse(Long userId);

    Optional<Coupon> findByCouponCodeForUpdate(String couponCode);

    void saveCoupon(Coupon updatedCoupon);

    UserCoupon saveUserCoupon(UserCoupon userCoupon);

    Optional<Coupon> findByUserIdAndCouponIdAndIsUsedFalse(Long userId, Long couponId);

    Optional<UserCoupon> findByUserIdAndCouponIdAndIsUsedFalseForUpdate(Long userId, Long couponId);
}
