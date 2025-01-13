package kr.hhplus.be.server.coupon.domain;

import kr.hhplus.be.server.common.exception.ErrorCode;
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
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.COUPON_NOT_FOUND_CODE));

        coupon.validateStock();

        Coupon updatedCoupon = coupon.incrementIssuedCount();

        couponRepository.saveCoupon(updatedCoupon);

        UserCoupon userCoupon = new UserCoupon(null, userId, coupon.id(), false);
        return couponRepository.saveUserCoupon(userCoupon);
    }

    public Coupon getUserCoupon(Long userId, Long couponId) {
        return couponRepository.findByUserIdAndCouponIdAndIsUsedFalse(userId, couponId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.COUPON_ALREADY_USED_OR_NOT_FOUND_CODE));
    }

    @Transactional
    public void useCoupon(Long userId, Long couponId) {
        UserCoupon userCoupon = couponRepository.findByUserIdAndCouponIdAndIsUsedFalseForUpdate(userId, couponId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.COUPON_ALREADY_USED_OR_NOT_FOUND_CODE));

        UserCoupon updatedUserCoupon = userCoupon.markAsUsed();

        couponRepository.saveUserCoupon(updatedUserCoupon);
    }
}
