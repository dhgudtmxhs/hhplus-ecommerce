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

    @Transactional(readOnly = true)
    public Point getPoint(Long userId) {
        return pointRepository.findByUserId(userId)
                .orElseGet(() -> pointRepository.save(Point.builder()
                        .userId(userId)
                        .point(0L)
                        .build()));
    }

    @Transactional
    public Point chargePoint(Long userId, Long amount) {
        Point.validatePoint(amount);

        Point point = pointRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.POINT_NOT_FOUND_CODE));

        point.charge(amount);

        return point;
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
