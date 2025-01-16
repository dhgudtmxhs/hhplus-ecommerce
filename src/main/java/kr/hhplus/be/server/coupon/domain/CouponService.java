package kr.hhplus.be.server.coupon.domain;

import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.coupon.infra.UserCouponJpaRepository;
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

    public Coupon getUserCoupon(Long userId, Long couponId) {
        return couponRepository.findByUserIdAndCouponIdAndIsUsedFalse(userId, couponId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.COUPON_ALREADY_USED_OR_NOT_FOUND_CODE));
    }

    @Transactional
    public UserCoupon issueCoupon(Long userId, Long couponId) {

        Coupon coupon = couponRepository.findByIdForUpdate(couponId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.COUPON_NOT_FOUND_CODE));

        couponRepository.findByCouponIdAndUserIdForUpdate(couponId, userId)
                .ifPresent(issuedCoupon -> {
                    throw new IllegalArgumentException(ErrorCode.COUPON_ALREADY_ISSUED_CODE);
                });

        coupon.issue();
        couponRepository.saveCoupon(coupon);

        UserCoupon userCoupon = UserCoupon.builder()
                .userId(userId)
                .couponId(coupon.getId())
                .isUsed(false)
                .build();

        return couponRepository.saveUserCoupon(userCoupon);
    }


    @Transactional
    public void useCoupon(Long userId, Long couponId) {
        UserCoupon userCoupon = couponRepository.findByUserIdAndCouponIdAndIsUsedFalseForUpdate(userId, couponId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.COUPON_ALREADY_USED_OR_NOT_FOUND_CODE));

        userCoupon.markAsUsed();

        couponRepository.saveUserCoupon(userCoupon);
    }


}
