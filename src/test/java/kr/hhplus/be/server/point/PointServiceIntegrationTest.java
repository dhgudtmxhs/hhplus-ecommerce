package kr.hhplus.be.server.point;
import kr.hhplus.be.server.point.domain.Point;
import kr.hhplus.be.server.point.domain.PointRepository;
import kr.hhplus.be.server.point.domain.PointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class PointServiceIntegrationTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private PointRepository pointRepository;

    private Long userId;
    private Point existingPoint;

    @BeforeEach
    void setUp() {
        userId = 1L;
        existingPoint = new Point(null, userId, 5000L);
        pointRepository.save(existingPoint);  // 실제 데이터베이스에 저장
    }

    @Test
    void 유효한_사용자_ID로_조회하면_포인트를_반환한다() {
        // When
        Point point = pointService.getPoint(userId);

        // Then
        assertNotNull(point);
        assertEquals(existingPoint.userId(), point.userId());
        assertEquals(existingPoint.point(), point.point());
    }

    @Test
    void 유효한_포인트_충전은_정상적으로_수행된다() {
        // Given
        Long chargeAmount = 3000L;
        Point chargedPoint = new Point(null, userId, existingPoint.point() + chargeAmount);

        // When
        Point result = pointService.chargePoint(userId, chargeAmount);

        // Then
        assertNotNull(result);
        assertEquals(chargedPoint.userId(), result.userId());
        assertEquals(chargedPoint.point(), result.point());
    }


}
