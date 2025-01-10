package kr.hhplus.be.server.order.infra;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.BaseEntity;
import kr.hhplus.be.server.coupon.infra.UserCouponEntity;
import kr.hhplus.be.server.order.domain.OrderStatus;
import kr.hhplus.be.server.user.infra.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "`order`")
public class OrderEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private UserEntity user;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    @Column(name = "final_price", nullable = false)
    private Long finalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_coupon_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private UserCouponEntity userCoupon;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> orderItems;

    @Builder
    public OrderEntity(UserEntity user, Long totalPrice, Long finalPrice, UserCouponEntity userCoupon, OrderStatus status, List<OrderItemEntity> orderItems) {
        this.user = user;
        this.totalPrice = totalPrice;
        this.finalPrice = finalPrice;
        this.userCoupon = userCoupon;
        this.status = status;
        this.orderItems = orderItems;
    }

    // setOrderItems 메서드 추가
    public void setOrderItems(List<OrderItemEntity> orderItems) {
        this.orderItems = orderItems;
        if (orderItems != null) {
            orderItems.forEach(orderItem -> orderItem.setOrder(this)); // 연관 관계 설정
        }
    }

    public void addOrderItem(OrderItemEntity orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

}