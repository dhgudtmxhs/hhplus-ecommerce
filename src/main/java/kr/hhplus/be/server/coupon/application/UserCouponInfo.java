package kr.hhplus.be.server.coupon.application;

public record UserCouponInfo(
        Long userId,
        Long couponId,
        Boolean isUsed
) {}
