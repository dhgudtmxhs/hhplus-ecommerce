package kr.hhplus.be.server.payment.application;

public record OrderData(
        Long orderId,
        Long userId,
        Long totalPrice,
        String status
){}