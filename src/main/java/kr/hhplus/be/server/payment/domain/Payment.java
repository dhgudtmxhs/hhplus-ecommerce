package kr.hhplus.be.server.payment.domain;

import kr.hhplus.be.server.point.domain.Point;

public record Payment(
        Long id,
        Long orderId,
        Long finalPrice,
        PaymentMethod paymentMethod,
        PaymentStatus status
) {
    public static Payment create(Long orderId, Long finalPrice, PaymentMethod paymentMethod, Point point) {
        PaymentStatus status = (point.point() >= finalPrice)
                ? PaymentStatus.SUCCESS
                : PaymentStatus.FAILED;
        return new Payment(null, orderId, finalPrice, paymentMethod, status);
    }
}
