package kr.hhplus.be.server.user.domain;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.server.common.entity.BaseEntity;
import kr.hhplus.be.server.common.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "`user`")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Builder
    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static void validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException(ErrorCode.USER_ID_NULL_CODE);
        }
        if (id <= 0) {
            throw new IllegalArgumentException(ErrorCode.USER_ID_INVALID_CODE);
        }
    }

}