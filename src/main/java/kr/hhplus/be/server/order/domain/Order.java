package kr.hhplus.be.server.order.domain;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.entity.BaseEntity;
import kr.hhplus.be.server.common.exception.ErrorCode;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Transient
    private List<OrderItem> orderItems;

    @Builder
    public Order(Long id, Long userId, Long totalPrice, OrderStatus status, List<OrderItem> orderItems) {
        this.id = id;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.status = status;
        this.orderItems = orderItems;
    }

    public static Order create(Long userId, List<OrderItem> orderItems) {

        Long totalPrice = orderItems.stream()
                .mapToLong(OrderItem::calculateTotalPrice)
                .sum();

        return Order.builder()
                .userId(userId)
                .totalPrice(totalPrice)
                .status(OrderStatus.CREATED)
                .orderItems(orderItems)
                .build();
    }

    public void markAsCompleted() {
        if (this.status != OrderStatus.CREATED) {
            throw new IllegalStateException(ErrorCode.ORDER_STATUS_CHANGE_INVALID_CODE);
        }
        this.status = OrderStatus.COMPLETED;
    }

    public void markAsCancelled() {
        if (this.status == OrderStatus.COMPLETED) {
            throw new IllegalStateException(ErrorCode.ORDER_CANCEL_INVALID_CODE);
        }
        this.status = OrderStatus.CANCELLED;
    }
}