package kr.hhplus.be.server.point.infra;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "point")
public class PointEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long point;

    @Builder
    public PointEntity(Long id, Long userId, Long point) {
        this.id = id;
        this.userId = userId;
        this.point = point;
    }

}
