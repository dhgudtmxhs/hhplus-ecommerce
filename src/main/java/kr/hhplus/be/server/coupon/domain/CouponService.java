package kr.hhplus.be.server.coupon.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public List<UserCoupon> getUserCoupons(Long userId) {
        return couponRepository.findByUserIdAndIsUsedFalse(userId);
    }

    @Transactional
    public UserCoupon issueCoupon(Long userId, String couponCode) {
        Coupon coupon = couponRepository.findByCouponCodeForUpdate(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("발급 가능한 쿠폰이 없습니다."));

        Coupon updatedCoupon = coupon.incrementIssuedCount();

        couponRepository.saveCoupon(updatedCoupon);

        UserCoupon userCoupon = new UserCoupon(null, userId, coupon.id(), false);
        return couponRepository.saveUserCoupon(userCoupon);
    }

    public Coupon getUserCoupon(Long userId, Long couponId) {
        return couponRepository.findByUserIdAndCouponIdAndIsUsedFalse(userId, couponId)
                .orElseThrow(() -> new IllegalArgumentException("해당 쿠폰이 사용되었거나 존재하지 않습니다."));
    }

    @Transactional
    public void useCoupon(Long userId, Long couponId) {
        UserCoupon userCoupon = couponRepository.findByUserIdAndCouponIdAndIsUsedFalseForUpdate(userId, couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰이 사용되었거나 존재하지 않습니다."));

        UserCoupon updatedUserCoupon = userCoupon.markAsUsed();

        couponRepository.saveUserCoupon(updatedUserCoupon);
    }
}
