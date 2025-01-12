package kr.hhplus.be.server.product.interfaces;

public record ProductResponse(
        String name,
        Long price,
        Long stock
) {}
