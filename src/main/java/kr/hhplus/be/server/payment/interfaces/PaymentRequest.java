package kr.hhplus.be.server.payment.interfaces;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.common.exception.ErrorCode;

public record PaymentRequest(
        @NotNull(message = ErrorCode.USER_ID_NULL_CODE)
        @Positive(message = ErrorCode.USER_ID_INVALID_CODE)
        Long userId,

        @NotNull(message = ErrorCode.ORDER_ID_NULL_CODE)
        @Positive(message = ErrorCode.ORDER_ID_INVALID_CODE)
        Long orderId,

        @NotNull(message = ErrorCode.ORDER_PRICE_NULL_CODE)
        @Positive(message = ErrorCode.ORDER_PRICE_INVALID_CODE)
        Long orderPrice,

        @Positive(message = ErrorCode.COUPON_ID_INVALID_CODE)
        Long couponId
) {}
