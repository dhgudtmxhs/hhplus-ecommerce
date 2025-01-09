package kr.hhplus.be.server.order.application;

public record ProductOrderCommand(
       Long productId,
       Long quantity
) {}
