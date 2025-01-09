package kr.hhplus.be.server.order.domain;

public record OrderItem(
        Long id,
        Long productId,
        Long quantity,
        Long price
) {}