package kr.hhplus.be.server.point.infra;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.entity.BaseEntity;
import kr.hhplus.be.server.user.infra.UserEntity;
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
public class PointEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private UserEntity user;

    private Long point;

    @Builder
    public PointEntity(Long id, UserEntity user, Long point) {
        this.id = id;
        this.user = user;
        this.point = point;
    }

}
