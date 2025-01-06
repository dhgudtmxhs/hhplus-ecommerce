package kr.hhplus.be.server.order.interfaces;

public record OrderRequest (
        String userId,
        String productId,
        int quantity,
        String couponId
){}
