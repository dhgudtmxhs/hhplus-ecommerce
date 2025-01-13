package kr.hhplus.be.server.order.domain;

import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.coupon.domain.Coupon;
import kr.hhplus.be.server.coupon.domain.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class Order {

    private final Long id;
    private final Long userId;
    private final Long totalPrice;
    private final Long finalPrice;
    private final Long userCouponId;
    private OrderStatus status;
    private final List<OrderItem> orderItems;

    public static Order create(Long userId, List<OrderItem> orderItems, Coupon coupon, Long usedPoints) {
        // 1. 총 주문 금액 계산
        Long totalPrice = orderItems.stream()
                .mapToLong(item -> item.price() * item.quantity())
                .sum();

        // 2. 쿠폰 할인 계산
        Long discountAmount = calculateDiscount(totalPrice, coupon);

        // 3. 최종 결제 금액 계산 (포인트 적용)
        Long finalPrice = totalPrice - discountAmount - usedPoints;
        if (finalPrice < 0) {
            throw new IllegalArgumentException(ErrorCode.ORDER_FINAL_PRICE_INVALID_CODE); // ErrorCode 사용
        }

        // 4. Order 객체 생성
        return new Order(
                null,
                userId,
                totalPrice,
                finalPrice,
                (coupon != null) ? coupon.id() : null,
                OrderStatus.PENDING,
                orderItems
        );
    }

    private static Long calculateDiscount(Long totalPrice, Coupon coupon) {
        if (coupon == null) {
            return 0L;
        }
        if (coupon.discountType() == DiscountType.FIXED) {
            return coupon.discountAmount();
        } else if (coupon.discountType() == DiscountType.PERCENT) {
            return totalPrice * coupon.discountAmount() / 100;
        }
        return 0L;
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