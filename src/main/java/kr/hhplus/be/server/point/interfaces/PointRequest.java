package kr.hhplus.be.server.point.interfaces;

public record PointRequest(
        String userId,
        Long amount
) {}