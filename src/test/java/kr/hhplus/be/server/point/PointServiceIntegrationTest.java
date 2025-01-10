package kr.hhplus.be.server.point;

import kr.hhplus.be.server.point.domain.Point;
import kr.hhplus.be.server.point.domain.PointRepository;
import kr.hhplus.be.server.point.domain.PointService;
import kr.hhplus.be.server.point.infra.PointEntity;
import kr.hhplus.be.server.point.infra.PointJpaRepository;
import kr.hhplus.be.server.user.infra.UserEntity;
import kr.hhplus.be.server.user.infra.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
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
    private UserJpaRepository userJpaRepository;

    private Long userId;
    private Point existingPoint;

    @BeforeEach
    @Transactional
    void setUp() {
        // Given: UserEntity와 PointEntity 저장
        UserEntity user = userJpaRepository.save(UserEntity.builder().build()); // ID 자동 생성
        userId = user.getId(); // 저장된 user ID 가져오기

        PointEntity pointEntity = pointJpaRepository.save(
                PointEntity.builder()
                        .user(user)
                        .point(5000L)
                        .build()
        );

        existingPoint = new Point(pointEntity.getId(), userId, pointEntity.getPoint());
    }

    @Test
    void findByUserId_정상_조회_확인() {
        // When: 기존에 저장된 userId로 PointEntity 조회
        Optional<PointEntity> result = pointJpaRepository.findByUserId(userId);

        // Then: 조회 결과 검증
        assertTrue(result.isPresent());
        assertEquals(existingPoint.id(), result.get().getId());
        assertEquals(existingPoint.point(), result.get().getPoint());
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

        // When
        Point result = pointService.chargePoint(userId, chargeAmount);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.userId());
        assertEquals(existingPoint.point() + chargeAmount, result.point());
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
        assertEquals(userId, result.userId());
        assertEquals(existingPoint.point() - deductAmount, result.point());
    }


}
