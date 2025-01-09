package kr.hhplus.be.server.point.infra;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PointJpaRepository extends JpaRepository<PointEntity, Long> {
    Optional<PointEntity> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PointEntity p where p.userId = :userId")
    Optional<PointEntity> findByUserIdForUpdate(@Param("userId") Long userId);
}
