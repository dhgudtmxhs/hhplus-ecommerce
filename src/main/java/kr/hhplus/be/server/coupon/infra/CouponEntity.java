package kr.hhplus.be.server.coupon.infra;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.BaseEntity;
import kr.hhplus.be.server.coupon.domain.DiscountType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "coupon")
public class CouponEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coupon_code")
    private String couponCode;

    // 할인 유형 (정액 할인, 비율 할인)
    @Column(name = "discount_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    @Column(name = "discount_amount", nullable = false)
    private Long discountAmount;

    @Column(name = "usage_limit", nullable = false)
    private Long usageLimit;

    @Column(name = "issued_count", nullable = false)
    private Long issuedCount;

    @Builder
    public CouponEntity(Long id, String couponCode, DiscountType discountType, Long discountAmount, Long usageLimit, Long issuedCount) {
        this.id = id;
        this.couponCode = couponCode;
        this.discountType = discountType;
        this.discountAmount = discountAmount;
        this.usageLimit = usageLimit;
        this.issuedCount = issuedCount;
    }

}
