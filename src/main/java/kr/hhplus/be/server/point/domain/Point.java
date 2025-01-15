package kr.hhplus.be.server.point.domain;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.entity.BaseEntity;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "point",
        uniqueConstraints = @UniqueConstraint(columnNames = "user_id")
)
public class Point extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long point;

    @Builder
    public Point(Long id, Long userId, Long point) {
        this.id = id;
        this.userId = userId;
        this.point = point;
    }

    private static final Long MAX_POINT = 10_000_000L;

    public static void validatePoint(Long point) {
        if (point == null) {
            throw new IllegalArgumentException(ErrorCode.POINT_CHARGE_AMOUNT_NULL_CODE);
        }
        if (point < 0) {
            throw new IllegalArgumentException(ErrorCode.POINT_CHARGE_AMOUNT_INVALID_CODE);
        }
    }

    public void charge(Long amount) {
        Long newPoint = this.point + amount;
        if (newPoint > MAX_POINT) {
            throw new IllegalArgumentException(ErrorCode.POINT_MAX_EXCEED_CODE);
        }
        this.point = newPoint;
    }

    public void deduct(Long amount) {
        if (this.point < amount) {
            throw new IllegalArgumentException(ErrorCode.POINT_DEDUCT_EXCEED_CODE);
        }
        this.point -= amount;
    }


}