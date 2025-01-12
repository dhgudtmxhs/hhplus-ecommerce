package kr.hhplus.be.server.point.domain;

import java.util.Optional;

public interface PointRepository {
    Optional<Point> findByUserId(Long userId);

    Optional<Point> findByUserIdForUpdate(Long userId);

    void save(Point updatedPoint);
}
