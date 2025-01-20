package kr.hhplus.be.server.order.interfaces;

import kr.hhplus.be.server.order.application.OrderItemInfo;
import kr.hhplus.be.server.product.domain.Product;

import java.util.List;

public record OrderResponse (
        Long id,
        Long userId,
        Long totalPrice,
        String status,
        List<OrderItemInfo> orderItems
){}
