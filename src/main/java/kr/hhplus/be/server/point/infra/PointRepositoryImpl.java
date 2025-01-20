package kr.hhplus.be.server.point.infra;

import kr.hhplus.be.server.point.domain.Point;
import kr.hhplus.be.server.point.domain.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {

    private final PointJpaRepository pointJpaRepository;

    @Override
    public Optional<Point> findByUserId(Long userId) {
        return pointJpaRepository.findByUserId(userId);
    }

    @Override
    public Optional<Point> findByUserIdForUpdate(Long userId) {
        return pointJpaRepository.findByUserIdForUpdate(userId);
    }

    @Override
    public Point save(Point updatedPoint) {
        return pointJpaRepository.save(updatedPoint);
    }

}
