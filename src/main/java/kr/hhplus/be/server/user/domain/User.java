package kr.hhplus.be.server.user.domain;

public record User(
        Long id,
        String name
) {
    public static void validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("사용자 ID는 null일 수 없습니다.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("사용자 ID는 음수이거나 0일 수 없습니다.");
        }
    }
}
