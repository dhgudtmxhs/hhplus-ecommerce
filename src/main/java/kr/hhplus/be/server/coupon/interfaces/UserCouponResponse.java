package kr.hhplus.be.server.coupon.interfaces;

public record UserCouponResponse(
        Long userId,
        Long couponId,
        Boolean isUsed
) {}
