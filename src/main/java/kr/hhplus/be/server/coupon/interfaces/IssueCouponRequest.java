package kr.hhplus.be.server.coupon.interfaces;

import jakarta.validation.constraints.NotNull;

public record IssueCouponRequest(
        @NotNull Long userId,
        @NotNull String couponCode
) {}
