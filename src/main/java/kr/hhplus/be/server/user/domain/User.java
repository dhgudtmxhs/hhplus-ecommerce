package kr.hhplus.be.server.user.domain;

import kr.hhplus.be.server.common.exception.ErrorCode;

public record User(
        Long id,
        String name
) {
    public User {
        if (id == null) {
            throw new IllegalArgumentException(ErrorCode.USER_ID_NULL_CODE);
        }
        if (id <= 0) {
            throw new IllegalArgumentException(ErrorCode.USER_ID_INVALID_CODE);
        }
    }
}
