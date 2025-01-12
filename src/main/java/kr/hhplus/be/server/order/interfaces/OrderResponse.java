package kr.hhplus.be.server.order.interfaces;

import kr.hhplus.be.server.product.domain.Product;

import java.util.List;

public record OrderResponse (
        Long orderId,
        Long finalPrice,
        String status,
        String paymentStatus,
        List<Product> products
){}
