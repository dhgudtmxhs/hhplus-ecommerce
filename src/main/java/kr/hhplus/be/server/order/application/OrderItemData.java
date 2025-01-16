package kr.hhplus.be.server.order.application;

public record OrderItemData(
        Long productId,
        String productName,
        Long quantity,
        Long price
) {}