package kr.hhplus.be.server.order.domain;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.entity.BaseEntity;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.order.domain.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "`order`")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    @Column(name = "final_price", nullable = false)
    private Long finalPrice;

    @Column(name = "user_coupon_id")
    private Long userCouponId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Transient
    private List<OrderItem> orderItems;

    @Builder
    public Order(Long id, Long userId, Long totalPrice, Long finalPrice, Long userCouponId, OrderStatus status, List<OrderItem> orderItems) {
        this.id = id;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.finalPrice = finalPrice;
        this.userCouponId = userCouponId;
        this.status = status;
        this.orderItems = orderItems;
    }

    public static Order create(Long userId, List<OrderItem> orderItems, Long userCouponId, Long discountAmount, Long usedPoints) {
        // 1. 총 주문 금액 계산
        Long totalPrice = orderItems.stream()
                .mapToLong(OrderItem::calculateTotalPrice)
                .sum();

        // 2. 최종 결제 금액 계산 (할인 금액 및 사용한 포인트 적용)
        Long finalPrice = totalPrice - discountAmount - usedPoints;
        if (finalPrice < 0) {
            throw new IllegalArgumentException(ErrorCode.ORDER_FINAL_PRICE_INVALID_CODE);
        }

        // 3. Order 객체 생성
        return Order.builder()
                .userId(userId)
                .totalPrice(totalPrice)
                .finalPrice(finalPrice)
                .userCouponId(userCouponId)
                .status(OrderStatus.PENDING)
                .orderItems(orderItems)
                .build();
    }

    // 상태 변경 메서드
    public void markAsPaid() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException(ErrorCode.ORDER_STATUS_CHANGE_INVALID_CODE);
        }
        this.status = OrderStatus.PAID;
    }

    public void markAsFailed() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException(ErrorCode.ORDER_STATUS_CHANGE_INVALID_CODE);
        }
        this.status = OrderStatus.FAILED;
    }

    public void cancelOrder() {
        if (this.status == OrderStatus.PAID) {
            throw new IllegalStateException(ErrorCode.ORDER_CANCEL_INVALID_CODE);
        }
        this.status = OrderStatus.CANCELLED;
    }
}