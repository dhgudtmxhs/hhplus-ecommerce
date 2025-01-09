package kr.hhplus.be.server.product.domain;

public record Product(
        Long id,
        String name,
        Long price,
        Long stock
) {
    public Product {
        if (price <= 0) {
            throw new IllegalArgumentException("가격은 0보다 커야 합니다.");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("재고는 0이상이어야 합니다.");
        }
    }

    public Product reduceStock(Long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("차감할 수량은 0보다 커야 합니다.");
        }
        if (this.stock < quantity) {
            throw new IllegalArgumentException("상품 ID " + this.id + "의 재고가 부족합니다. 현재 재고: " + this.stock);
        }
        return new Product(this.id, this.name, this.price, this.stock - quantity);
    }

    public Product addStock(Long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("추가할 수량은 0보다 커야 합니다.");
        }
        return new Product(this.id, this.name, this.price, this.stock + quantity);
    }

}
