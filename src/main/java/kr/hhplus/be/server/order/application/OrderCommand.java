package kr.hhplus.be.server.order.application;

import java.util.List;

public record OrderCommand(
        Long userId,
        List<ProductOrderCommand> products,
        Long couponId
) {}
