package kr.hhplus.be.server.product.application;

public record ProductInfo(
        String name,
        Long price,
        Long stock
) {}
