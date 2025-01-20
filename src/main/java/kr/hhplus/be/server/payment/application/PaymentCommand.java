package kr.hhplus.be.server.payment.application;

public record PaymentCommand(
        Long userId,
        Long orderId,
        Long orderPrice,
        Long couponId // 쿠폰 ID 추가
) {}