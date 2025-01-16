package kr.hhplus.be.server.product.domain;


import jakarta.persistence.*;
import kr.hhplus.be.server.common.entity.BaseEntity;
import kr.hhplus.be.server.common.exception.ErrorCode;
import lombok.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "product")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Long stock;

    @Builder
    public Product(Long id, String name, Long price, Long stock) {
        validateProduct(price, stock);
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    private void validateProduct(Long price, Long stock) {
        if (price <= 0) {
            throw new IllegalArgumentException(ErrorCode.PRODUCT_PRICE_INVALID_CODE);
        }
        if (stock < 0) {
            throw new IllegalArgumentException(ErrorCode.PRODUCT_STOCK_INVALID_CODE);
        }
    }

    public void reduceStock(Long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(ErrorCode.PRODUCT_QUANTITY_INVALID_CODE);
        }
        if (this.stock < quantity) {
            throw new IllegalArgumentException(ErrorCode.PRODUCT_STOCK_INSUFFICIENT_CODE);
        }
        this.stock -= quantity;
    }

}