package kr.hhplus.be.server.product.interfaces;

import java.util.List;

public record ProductListResponse(
        List<ProductResponse> products,
        int page,
        int size,
        Long total,
        Long totalPages
) {}
