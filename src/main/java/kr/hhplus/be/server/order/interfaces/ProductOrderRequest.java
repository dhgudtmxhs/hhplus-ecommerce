package kr.hhplus.be.server.order.interfaces;

public record ProductOrderRequest (
        String productId,
        Long quantity
){}
