package kr.hhplus.be.server.payment.application;

public record PaymentInfo(
        Long id,
        Long orderId,
        Long orderPrice,
        Long finalPrice,
        String status
) {}