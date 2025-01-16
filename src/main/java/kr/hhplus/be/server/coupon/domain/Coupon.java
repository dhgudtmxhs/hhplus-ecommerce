package kr.hhplus.be.server.coupon.domain;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.entity.BaseEntity;
import kr.hhplus.be.server.common.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "coupon")
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coupon_code", nullable = false)
    private String couponCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;

    @Column(name = "discount_amount", nullable = false)
    private Long discountAmount;

    @Column(name = "usage_limit", nullable = false)
    private Long usageLimit;

    @Column(name = "issued_count", nullable = false)
    private Long issuedCount;

    @Builder
    public Coupon(Long id, String couponCode, DiscountType discountType, Long discountAmount, Long usageLimit, Long issuedCount) {
        this.id = id;
        this.couponCode = couponCode;
        this.discountType = discountType;
        this.discountAmount = discountAmount;
        this.usageLimit = usageLimit;
        this.issuedCount = issuedCount;
    }

    public void issue() {
        if (this.issuedCount >= this.usageLimit) {
            throw new IllegalStateException(ErrorCode.COUPON_STOCK_INSUFFICIENT_CODE);
        }
        this.issuedCount += 1;
    }

    public Long calculateDiscount(Long orderPrice) {
        if (discountType == DiscountType.FIXED) {
            return Math.min(discountAmount, orderPrice); // 고정 할인은 최대 주문 금액까지만 적용
        } else if (discountType == DiscountType.PERCENT) {
            return Math.round(orderPrice * (discountAmount / 100.0)); // 비율 할인 계산
        }
        return 0L; // 알 수 없는 할인 유형일 경우 0 반환
    }
}