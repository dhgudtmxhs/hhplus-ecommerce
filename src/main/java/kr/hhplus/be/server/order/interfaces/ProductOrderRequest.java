package kr.hhplus.be.server.order.interfaces;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.common.exception.ErrorCode;

public record ProductOrderRequest (
        @NotNull(message = ErrorCode.PRODUCT_ID_NULL_CODE)
        @Positive(message = ErrorCode.PRODUCT_NOT_FOUND_CODE)
        Long productId,

        @NotNull(message = ErrorCode.PRODUCT_QUANTITY_NULL_CODE)
        @Positive(message = ErrorCode.PRODUCT_QUANTITY_INVALID_CODE)
        Long quantity
){}
