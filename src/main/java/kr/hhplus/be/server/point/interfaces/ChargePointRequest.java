package kr.hhplus.be.server.point.interfaces;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ChargePointRequest(
        @NotNull Long userId,
        @NotNull @Positive Long amount
) {}