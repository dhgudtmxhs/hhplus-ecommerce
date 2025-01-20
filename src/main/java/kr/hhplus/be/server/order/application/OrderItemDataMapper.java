package kr.hhplus.be.server.order.application;

import kr.hhplus.be.server.product.domain.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
public interface OrderItemDataMapper {
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.price", target = "price")
    @Mapping(source = "quantity", target = "quantity")
    OrderItemData toOrderItemData(Product product, Long quantity);
}