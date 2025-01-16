package kr.hhplus.be.server.payment.interfaces;

public record PaymentResponse(
        Long id,
        Long orderId,
        Long orderPrice,
        Long finalPrice,
        String status
) {}

