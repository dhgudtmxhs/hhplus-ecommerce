package kr.hhplus.be.server.coupon.domain;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.entity.BaseEntity;
import kr.hhplus.be.server.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "user_coupon",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "coupon_id"})
        }
)
public class UserCoupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;

    @Builder
    public UserCoupon(Long id, Long userId, Long couponId, Boolean isUsed) {
        this.id = id;
        this.userId = userId;
        this.couponId = couponId;
        this.isUsed = isUsed;
    }

    public void markAsUsed() {
        if (this.isUsed) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }
        this.isUsed = true;
    }
}