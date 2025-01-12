package kr.hhplus.be.server.product.interfaces;

import kr.hhplus.be.server.product.application.ProductInfo;
import kr.hhplus.be.server.product.application.ProductListInfo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductResponseMapper {
    ProductResponse toProductResponse(ProductInfo productInfo);
    List<ProductResponse> toProductResponseList(List<ProductInfo> productInfos);
    ProductListResponse toProductListResponse(ProductListInfo info);

}
