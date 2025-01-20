package kr.hhplus.be.server.order.interfaces;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import kr.hhplus.be.server.common.exception.ErrorCode;

import java.util.List;

public record OrderRequest (
        @NotNull(message = ErrorCode.USER_ID_NULL_CODE)
        @Positive(message = ErrorCode.USER_ID_INVALID_CODE)
        Long userId,

        @Size(min = 1, message = ErrorCode.PRODUCTS_LIST_EMPTY_CODE)
        List<ProductOrderRequest> products
){}
