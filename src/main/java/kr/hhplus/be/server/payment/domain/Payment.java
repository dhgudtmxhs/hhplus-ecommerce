package kr.hhplus.be.server.payment.domain;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.entity.BaseEntity;
import kr.hhplus.be.server.point.domain.Point;
import lombok.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "payment")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="order_id", nullable = false)
    private Long orderId;

    @Column(name = "final_price", nullable = false)
    private Long finalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name="payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private Long point;

    @Builder
    public Payment(Long id, Long orderId, Long finalPrice, PaymentMethod paymentMethod, PaymentStatus status, Long point) {
        this.id = id;
        this.orderId = orderId;
        this.finalPrice = finalPrice;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.point = point;
    }

    /**
     * 결제 생성 메서드
     *
     * @param orderId 주문 ID
     * @param finalPrice 최종 결제 금액
     * @param paymentMethod 결제 방법
     * @param point 사용 포인트
     * @return Payment 인스턴스
     */
    public static Payment create(Long orderId, Long finalPrice, PaymentMethod paymentMethod, Long point) {
        PaymentStatus status = (point >= finalPrice)
                ? PaymentStatus.SUCCESS
                : PaymentStatus.FAILED;
        return Payment.builder()
                .orderId(orderId)
                .finalPrice(finalPrice)
                .paymentMethod(paymentMethod)
                .status(status)
                .point(point)
                .build();
    }

    /**
     * 결제 상태를 성공으로 변경
     */
    public void markAsSuccess() {
        this.status = PaymentStatus.SUCCESS;
    }

    /**
     * 결제 상태를 실패로 변경
     */
    public void markAsFailed() {
        this.status = PaymentStatus.FAILED;
    }

}