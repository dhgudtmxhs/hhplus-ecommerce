package kr.hhplus.be.server.coupon.application;

import kr.hhplus.be.server.coupon.domain.DiscountType;

public record CouponInfo(
        String couponCode,
        DiscountType discountType,
        Long discountAmount,
        Long usageLimit,
        Long issuedCount
) {}
