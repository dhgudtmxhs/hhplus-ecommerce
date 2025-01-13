package kr.hhplus.be.server.payment.infra;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.entity.BaseEntity;
import kr.hhplus.be.server.payment.domain.PaymentMethod;
import kr.hhplus.be.server.payment.domain.PaymentStatus;
import lombok.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "payment")
public class PaymentEntity extends BaseEntity {

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
    @Column(name="status", nullable = false)
    private PaymentStatus status;


    @Builder
    public PaymentEntity(Long id, Long orderId, Long finalPrice, PaymentMethod paymentMethod, PaymentStatus status) {
        this.id = id;
        this.orderId = orderId;
        this.finalPrice = finalPrice;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

}