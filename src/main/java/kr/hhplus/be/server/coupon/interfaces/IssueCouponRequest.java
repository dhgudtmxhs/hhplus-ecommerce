package kr.hhplus.be.server.coupon.interfaces;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.common.exception.ErrorCode;

public record IssueCouponRequest(
        @NotNull(message = ErrorCode.USER_ID_NULL_CODE)
        @Positive(message = ErrorCode.USER_ID_INVALID_CODE)
        Long userId,

        @NotNull(message = ErrorCode.COUPON_CODE_NULL_CODE)
        String couponCode
) {}
