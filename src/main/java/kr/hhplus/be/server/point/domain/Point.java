package kr.hhplus.be.server.point.domain;

import kr.hhplus.be.server.common.exception.ErrorCode;

public record Point(
        Long id,
        Long userId,
        Long point
) {

    // 최대 포인트
    private static final Long MAX_POINT = 10_000_000L;

    // 포인트 충전
    public Point chargePoint(Long amount) {
        Long newPoint = this.point + amount;
        if (newPoint > MAX_POINT) {
            throw new IllegalArgumentException(ErrorCode.POINT_MAX_EXCEED_CODE);
        }
        return new Point(this.id, this.userId, newPoint);
    }

    // 포인트 검증
    public static void validatePoint(Long point) {
        if (point == null) {
            throw new IllegalArgumentException(ErrorCode.POINT_CHARGE_AMOUNT_NULL_CODE);
        }
        if (point < 0) {
            throw new IllegalArgumentException(ErrorCode.POINT_CHARGE_AMOUNT_INVALID_CODE);
        }
    }

    // 포인트 차감
    public Point deduct(Long amount) {
        if (this.point < amount) {
            throw new IllegalArgumentException(ErrorCode.POINT_DEDUCT_EXCEED_CODE);
        }
        return new Point(this.id, this.userId, this.point - amount); // id 유지
    }

}
