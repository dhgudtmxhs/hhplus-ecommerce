package kr.hhplus.be.server.point.application;

public record ChargePointCommand(
        Long userId,
        Long amount
) {}