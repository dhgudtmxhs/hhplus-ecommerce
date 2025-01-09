package kr.hhplus.be.server.point.domain;

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
            throw new IllegalArgumentException("충전 후 포인트는 최대 천만원을 초과할 수 없습니다.");
        }
        return new Point(this.id, this.userId, newPoint);
    }

    // 포인트 검증
    public static void validatePoint(Long point) {
        if (point == null) {
            throw new IllegalArgumentException("포인트는 null일 수 없습니다.");
        }
        if (point <= 0) {
            throw new IllegalArgumentException("포인트는 음수일 수 없습니다.");
        }
    }

    public Point deduct(Long amount) {
        if (this.point < amount) {
            throw new IllegalArgumentException("차감할 포인트가 보유 포인트보다 많습니다.");
        }
        return new Point(null, this.userId, this.point - amount);
    }


}
