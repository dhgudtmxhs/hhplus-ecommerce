package kr.hhplus.be.server.order.application;

public record OrderItemInfo(
        Long productId,
        String productName,
        Long quantity,
        Long price
) {}
