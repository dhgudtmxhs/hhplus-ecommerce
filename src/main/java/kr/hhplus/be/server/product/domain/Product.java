package kr.hhplus.be.server.product.domain;

import kr.hhplus.be.server.common.exception.ErrorCode;

public record Product(
        Long id,
        String name,
        Long price,
        Long stock
) {
    public Product {
        if (price <= 0) {
            throw new IllegalArgumentException(ErrorCode.PRODUCT_PRICE_INVALID_CODE);
        }
        if (stock < 0) {
            throw new IllegalArgumentException(ErrorCode.PRODUCT_STOCK_INVALID_CODE);
        }
    }

    public Product reduceStock(Long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(ErrorCode.PRODUCT_QUANTITY_INVALID_CODE);
        }
        if (this.stock < quantity) {
            throw new IllegalArgumentException(ErrorCode.PRODUCT_STOCK_INSUFFICIENT_CODE);
        }
        return new Product(this.id, this.name, this.price, this.stock - quantity);
    }

    public Product addStock(Long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(ErrorCode.PRODUCT_QUANTITY_INVALID_CODE);
        }
        return new Product(this.id, this.name, this.price, this.stock + quantity);
    }
}