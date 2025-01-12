package kr.hhplus.be.server.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND("USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    MAX_BALANCE_EXCEEDED("MAX_BALANCE_EXCEEDED", "최대 잔액을 초과했습니다."),
    INVALID_INPUT("INVALID_INPUT", "잘못된 입력입니다.");

    private final String code;
    private final String message;
}