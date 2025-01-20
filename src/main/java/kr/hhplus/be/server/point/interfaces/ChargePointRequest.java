package kr.hhplus.be.server.point.interfaces;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.common.exception.ErrorCode;

public record ChargePointRequest(
        @NotNull(message = ErrorCode.USER_ID_NULL_CODE)
        @Positive(message = ErrorCode.USER_ID_INVALID_CODE)
        Long userId,

        @NotNull(message = ErrorCode.POINT_CHARGE_AMOUNT_NULL_CODE)
        @Positive(message = ErrorCode.POINT_CHARGE_AMOUNT_INVALID_CODE)
        Long amount
) {}