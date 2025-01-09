package kr.hhplus.be.server.order.application;

import kr.hhplus.be.server.product.domain.Product;

import java.util.List;

public record OrderInfo (
        Long orderId,
        Long finalPrice,
        String status,
        String paymentStatus,
        List<Product> products
) {}