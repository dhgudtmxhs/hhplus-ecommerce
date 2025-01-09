package kr.hhplus.be.server.order.interfaces;

import java.util.List;

public record OrderRequest (
        Long userId,
        List<ProductOrderRequest> products,
        Long couponId
){}
