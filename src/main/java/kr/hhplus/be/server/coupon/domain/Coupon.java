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
}