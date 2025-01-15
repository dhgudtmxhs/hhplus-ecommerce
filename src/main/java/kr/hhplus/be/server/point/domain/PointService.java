package kr.hhplus.be.server.point.domain;

import kr.hhplus.be.server.common.exception.ErrorCode;
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
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.POINT_NOT_FOUND_CODE));
    }

    @Transactional
    public Point chargePoint(Long userId, Long amount) {
        Point.validatePoint(amount);

        Point point = pointRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.POINT_NOT_FOUND_CODE));

        point.charge(amount);
        pointRepository.save(point);

        return point;
    }

    @Transactional
    public Point deductPoint(Long userId, Long amount) {
        Point.validatePoint(amount);

        Point point = pointRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.POINT_NOT_FOUND_CODE));

        point.deduct(amount);
        pointRepository.save(point);

        return point;
    }
}
