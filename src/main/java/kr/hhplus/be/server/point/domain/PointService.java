package kr.hhplus.be.server.point.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    public Point getPoint(Long userId) {
        return pointRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자의 포인트를 찾을 수 없습니다."));
    }

    @Transactional
    public Point chargePoint(Long userId, Long amount) {

        Point.validatePoint(amount);

        Point point = pointRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자의 포인트를 찾을 수 없습니다."));

        Point updatedPoint = point.chargePoint(amount);

        pointRepository.save(updatedPoint);

        return updatedPoint;
    }

    @Transactional
    public Point deductPoint(Long userId, Long amount) {
        Point.validatePoint(amount);

        Point point = pointRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자의 포인트를 찾을 수 없습니다."));

        Point updatedPoint = point.deduct(amount);

        pointRepository.save(updatedPoint);

        return updatedPoint;
    }
}
