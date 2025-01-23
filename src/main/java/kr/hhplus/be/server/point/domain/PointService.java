package kr.hhplus.be.server.point.domain;

import jakarta.persistence.OptimisticLockException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    @Transactional(readOnly = true)
    public Point getPoint(Long userId) {
        return pointRepository.findByUserId(userId)
                .orElseGet(() -> pointRepository.save(Point.builder()
                        .userId(userId)
                        .point(0L)
                        .build()));
    }

    @Retryable(
            value = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class},
            maxAttempts = 5, // 재시도 횟수
            backoff = @Backoff(delay = 100, multiplier = 2) // 지수 백오프 적용
    )
    @Transactional
    public Point chargePoint(Long userId, Long amount) {
        Point.validatePoint(amount);

        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.POINT_NOT_FOUND_CODE));

        point.charge(amount);

        return pointRepository.save(point);
    }


    public Point findPointForUpdate(Long userId) {
        return pointRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.POINT_NOT_FOUND_CODE));
    }

    public Point deductPoint(Point point, Long amount) {
        Point.validatePoint(amount);

        point.deduct(amount);

        return pointRepository.save(point); // 변경 후 저장
    }
}
