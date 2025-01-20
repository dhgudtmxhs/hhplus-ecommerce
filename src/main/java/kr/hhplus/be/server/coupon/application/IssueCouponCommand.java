package kr.hhplus.be.server.coupon.application;

public record IssueCouponCommand(
        Long userId,
        Long couponId
){}
