package kr.hhplus.be.server.product.application;

import java.util.List;

public record ProductListInfo(
        List<ProductInfo> products,
        int page,
        int size,
        long total,
        long totalPages
) {}