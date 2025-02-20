package kr.hhplus.be.server.payment.application.event;

public record OrderDataMessage(
        Long orderId,
        Long userId,
        Long totalPrice,
        String orderStatus
) {}
