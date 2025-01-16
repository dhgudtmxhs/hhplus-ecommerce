package kr.hhplus.be.server.payment.domain;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.entity.BaseEntity;
import kr.hhplus.be.server.common.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "payment")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "order_price", nullable = false)
    private Long orderPrice;

    @Column(name = "final_price", nullable = false)
    private Long finalPrice;

    @Column(name = "coupon_id") // nullable, 쿠폰이 없을 수 있음
    private Long couponId;

    @Builder
    public Payment(Long id, Long orderId, PaymentStatus status, Long orderPrice, Long finalPrice, Long couponId) {
        validatePrices(orderPrice, finalPrice); // 생성 시 가격 검증
        this.id = id;
        this.orderId = orderId;
        this.status = status;
        this.orderPrice = orderPrice;
        this.finalPrice = finalPrice;
        this.couponId = couponId;
    }

    private void validatePrices(Long orderPrice, Long finalPrice) {
        if (orderPrice < 0) {
            throw new IllegalArgumentException(ErrorCode.ORDER_PRICE_INVALID_CODE);
        }
        if (finalPrice < 0) {
            throw new IllegalArgumentException(ErrorCode.PAY_PRICE_INVALID_CODE);
        }
    }

    public static Payment create(Long orderId, Long orderPrice, Long finalPrice, Long couponId) {
        return Payment.builder()
                .orderId(orderId)
                .orderPrice(orderPrice)
                .finalPrice(finalPrice)
                .couponId(couponId)
                .status(PaymentStatus.SUCCESS)
                .build();
    }

}