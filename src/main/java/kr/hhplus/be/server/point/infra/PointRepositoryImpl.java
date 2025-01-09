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
    private final PointMapper pointMapper;

    @Override
    public Optional<Point> findByUserId(Long userId) {
        return pointJpaRepository.findByUserId(userId)
                .map(pointMapper::toDomain);
    }

    @Override
    public Optional<Point> findByUserIdForUpdate(Long userId) {
        return pointJpaRepository.findByUserIdForUpdate(userId)
                .map(pointMapper::toDomain);
    }

    @Override
    public void save(Point updatedPoint) {
        pointJpaRepository.save(pointMapper.toEntity(updatedPoint));
    }

}
