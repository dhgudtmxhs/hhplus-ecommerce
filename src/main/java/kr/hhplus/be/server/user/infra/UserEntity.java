package kr.hhplus.be.server.user.infra;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.server.common.entity.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "`user`")
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Builder
    public UserEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}