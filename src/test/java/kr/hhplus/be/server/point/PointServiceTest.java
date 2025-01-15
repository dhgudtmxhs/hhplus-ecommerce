package kr.hhplus.be.server.point;

import kr.hhplus.be.server.point.domain.Point;
import kr.hhplus.be.server.point.domain.PointRepository;
import kr.hhplus.be.server.point.domain.PointService;
import kr.hhplus.be.server.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PointServiceTest {

    @Mock
    private PointRepository pointRepository;

    @InjectMocks
    private PointService pointService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 유효한_사용자_ID로_조회하면_포인트를_반환한다() {
        // Given
        Long userId = 1L;
        Point expectedPoint = new Point(1L, 1L, 5000L);
        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(expectedPoint));

        // When
        Point point = pointService.getPoint(userId);

        // Then
        assertEquals(expectedPoint, point);
        verify(pointRepository, times(1)).findByUserId(userId);
    }

    @Test
    void 존재하지_않는_사용자_ID로_조회하면_NoSuchElementException_예외를_반환한다() {
        // Given
        Long userId = 10L;
        when(pointRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> pointService.getPoint(userId));
    }

    @Test
    void 유효한_포인트_충전은_정상적으로_수행된다() {
        // Given
        Long userId = 1L;
        Long chargeAmount = 3_000L;
        Point point = new Point(1L, 1L, 5_000L);

        when(pointRepository.findByUserIdForUpdate(userId)).thenReturn(Optional.of(point));

        // When
        Point result = pointService.chargePoint(userId, chargeAmount);

        // Then
        verify(pointRepository).save(any(Point.class)); // 저장 메서드가 호출되었는지 확인

        // 개별 필드 값 비교
        assertEquals(8_000L, result.getPoint());  // 충전 후 포인트 값 비교
        assertEquals(userId, result.getUserId());  // 사용자 ID 비교
    }

    @Test
    void 존재하지_않는_사용자의_포인트를_충전하려_하면_NoSuchElementException_예외가_발생한다() {
        // Given
        Long userId = 1L;
        Long chargeAmount = 3_000L;
        when(pointRepository.findByUserIdForUpdate(userId)).thenReturn(Optional.empty());

        // When && Then
        assertThrows(NoSuchElementException.class, () -> pointService.chargePoint(userId, chargeAmount));
    }

    @Test
    void 유효한_포인트_차감은_정상적으로_수행된다() {
        // Given
        Long userId = 1L;
        Long deductAmount = 3_000L;
        Point point = new Point(1L, 1L, 5_000L);

        when(pointRepository.findByUserIdForUpdate(userId)).thenReturn(Optional.of(point));

        // When
        Point result = pointService.deductPoint(userId, deductAmount);

        // Then
        verify(pointRepository).save(any(Point.class));

        // 개별 필드 값 비교
        assertEquals(2_000L, result.getPoint());
        assertEquals(userId, result.getUserId());
    }

    @Test
    void 존재하지_않는_사용자의_포인트를_차감하려_하면_NoSuchElementException_예외가_발생한다() {
        // Given
        Long userId = 1L;
        Long deductAmount = 3_000L;
        when(pointRepository.findByUserIdForUpdate(userId)).thenReturn(Optional.empty());

        // When && Then
        assertThrows(NoSuchElementException.class, () -> pointService.deductPoint(userId, deductAmount));
    }

    @Test
    void 차감할_포인트가_보유_포인트보다_많으면_IllegalArgumentException_예외가_발생한다() {
        // Given
        Long userId = 1L;
        Long deductAmount = 6_000L;
        Point point = new Point(1L, 1L, 5_000L);

        when(pointRepository.findByUserIdForUpdate(userId)).thenReturn(Optional.of(point));

        // When && Then
        assertThrows(IllegalArgumentException.class, () -> pointService.deductPoint(userId, deductAmount));
    }



}
