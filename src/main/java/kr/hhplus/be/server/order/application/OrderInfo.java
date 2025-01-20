package kr.hhplus.be.server.order.application;

import java.util.List;

public record OrderInfo(
        Long id,
        Long userId,
        Long totalPrice,
        String status,
        List<OrderItemInfo> orderItems
) {}