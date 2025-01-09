package kr.hhplus.be.server.product.application;

import kr.hhplus.be.server.product.domain.Product;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductInfoMapper {

    ProductInfo toProductInfo(Product product);

    List<ProductInfo> toProductInfos(List<Product> popularProducts);

    default ProductListInfo toProductListInfo(List<Product> products, int page, int size, long total, long totalPages) {
        List<ProductInfo> productInfos = products.stream()
                .map(this::toProductInfo)
                .toList();
        return new ProductListInfo(productInfos, page, size, total, totalPages);
    }



}
