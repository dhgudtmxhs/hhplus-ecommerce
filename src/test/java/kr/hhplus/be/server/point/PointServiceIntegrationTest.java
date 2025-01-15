package kr.hhplus.be.server.point;

import kr.hhplus.be.server.point.domain.Point;
import kr.hhplus.be.server.point.domain.PointRepository;
import kr.hhplus.be.server.point.domain.PointService;
import kr.hhplus.be.server.point.infra.PointJpaRepository;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.infra.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class PointServiceIntegrationTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private PointJpaRepository pointJpaRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    private Long userId;
    private Point existingPoint;

    @BeforeEach
    @Transactional
    void setUp() {
        // Given
        User user = userJpaRepository.save(User.builder()
                .name("ohs")
                .build());
        userId = user.getId();

        Point point = pointJpaRepository.save(
                Point.builder()
                        .userId(userId)
                        .point(5000L)
                        .build()
        );
        existingPoint = new Point(point.getId(), 1L, point.getPoint());
    }

    @Test
    void 유효한_사용자_ID로_조회하면_포인트를_반환한다() {
        // When
        Point point = pointService.getPoint(userId);

        // Then
        assertNotNull(point);
        assertEquals(existingPoint.getPoint(), point.getPoint());
    }

    @Test
    @Transactional
    void 포인트_조회시_포인트가_없으면_생성된다() {

        // given
        pointJpaRepository.deleteAll();

        // When
        Point point = pointService.getPoint(userId);

        // Then
        assertNotNull(point);
        assertEquals(userId, point.getUserId());
        assertEquals(0L, point.getPoint());

        // 저장된 포인트가 실제로 DB에 있는지 확인
        Optional<Point> savedPoint = pointRepository.findByUserId(userId);
        assertTrue(savedPoint.isPresent());
        assertEquals(0L, savedPoint.get().getPoint());
    }

    @Test
    void 유효한_포인트_충전은_정상적으로_수행된다() {
        // Given
        Long chargeAmount = 3000L;

        // When
        Point result = pointService.chargePoint(userId, chargeAmount);

        // Then
        assertNotNull(result);
        assertEquals(existingPoint.getPoint() + chargeAmount, result.getPoint());
    }

    @Test
    void 유효한_포인트_차감은_정상적으로_수행된다() {
        // Given
        Long deductAmount = 2000L;

        // When
        pointService.deductPoint(userId, deductAmount);

        // Then
        Point result = pointService.getPoint(userId);

        assertNotNull(result);
        assertEquals(existingPoint.getPoint() - deductAmount, result.getPoint());
    }


}
