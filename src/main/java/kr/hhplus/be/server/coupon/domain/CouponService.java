package kr.hhplus.be.server.coupon.domain;

import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.common.redis.coupon.RedisCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final RedisCouponService redisCouponService;

    public List<UserCoupon> getUserCoupons(Long userId) {
        return couponRepository.findByUserIdAndIsUsedFalse(userId);
    }

    @Transactional(readOnly = true)
    public Coupon getUserCoupon(Long userId, Long couponId) {
        return couponRepository.findByUserIdAndCouponIdAndIsUsedFalse(userId, couponId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.COUPON_ALREADY_USED_OR_NOT_FOUND_CODE));
    }

    @Transactional
    public UserCoupon issueCoupon(Long userId, Long couponId) {

        CouponIssueResult redisResult = redisCouponService.requestCoupon(userId, couponId);

        switch (redisResult) {
            case COUPON_ALREADY_ISSUED:
                throw new IllegalArgumentException(ErrorCode.COUPON_ALREADY_ISSUED_CODE);
            case COUPON_ISSUE_CLOSED:
                throw new IllegalArgumentException(ErrorCode.COUPON_ISSUE_CLOSED_CODE);
            case COUPON_ISSUED:
                break;
            default:
                throw new IllegalArgumentException(ErrorCode.COUPON_ISSUE_FAILED_CODE);
        }

        try {
            Coupon coupon = couponRepository.findCouponById(couponId)
                    .orElseThrow(() -> new IllegalArgumentException(ErrorCode.COUPON_NOT_FOUND_CODE));

            coupon.issue();
            couponRepository.saveCoupon(coupon);

            UserCoupon userCoupon = UserCoupon.builder()
                    .userId(userId)
                    .couponId(coupon.getId())
                    .isUsed(false)
                    .build();

            return couponRepository.saveUserCoupon(userCoupon);

        } catch (Exception e) {
            redisCouponService.cancelCoupon(userId, couponId);
            throw e;
        }
    }

    public Coupon useCoupon(Long userId, Long couponId) {
        UserCoupon userCoupon = couponRepository.findByUserIdAndCouponIdAndIsUsedFalseForUpdate(userId, couponId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.COUPON_ALREADY_USED_OR_NOT_FOUND_CODE));

        userCoupon.markAsUsed();

        couponRepository.saveUserCoupon(userCoupon);

        return couponRepository.findCouponById(userCoupon.getCouponId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.COUPON_NOT_FOUND_CODE));

    }


}
