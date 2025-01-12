package kr.hhplus.be.server.point;

import kr.hhplus.be.server.point.domain.Point;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PointTest {

    @Test
    void 유효한_포인트일_경우_검증시_예외가_발생하지_않는다() {
        // Given && When && Then
        assertDoesNotThrow(() -> Point.validatePoint(1_000L));
    }

    @Test
    void 포인트가_null이거나_음수인_경우_예외가_발생한다() {
        // Given && When && Then
        assertThrows(IllegalArgumentException.class, () -> Point.validatePoint(null));
        assertThrows(IllegalArgumentException.class, () -> Point.validatePoint(-1L));
    }

    @Test
    void 유효한_포인트_충전은_정상적으로_수행된다() {
        // Given
        Point point = new Point(1L, 1L, 5_000L);
        Long chargeAmount = 3_000L;

        // When
        Point updatedPoint = point.chargePoint(chargeAmount);

        // Then
        assertEquals(8_000L, updatedPoint.point());
    }

    @Test
    void 충전_후_포인트가_최대값을_초과하면_IllegalArgumentException_예외가_발생한다() {
        // Given
        Point point = new Point(1L, 1L, 9_500_000L);
        Long chargeAmount = 600_000L;

        // When && Then
        assertThrows(IllegalArgumentException.class, () -> point.chargePoint(chargeAmount));
    }

    @Test
    void 유효한_포인트_차감은_정상적으로_수행된다() {
        // Given
        Point point = new Point(1L, 1L, 5_000L);
        Long deductAmount = 3_000L;

        // When
        Point updatedPoint = point.deduct(deductAmount);

        // Then
        assertEquals(2_000L, updatedPoint.point());
    }

    @Test
    void 차감할_포인트가_보유_포인트보다_많으면_IllegalArgumentException_예외가_발생한다() {
        // Given
        Point point = new Point(1L, 1L, 3_000L);
        Long deductAmount = 5_000L;

        // When && Then
        assertThrows(IllegalArgumentException.class, () -> point.deduct(deductAmount));
    }

}
