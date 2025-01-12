package kr.hhplus.be.server.product.infra;

import kr.hhplus.be.server.product.domain.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface ProductMapper {
    Product toDomain(ProductEntity productEntity);
    List<Product> toDomainList(List<ProductEntity> entities);
    ProductEntity toEntity(Product product);
    List<ProductEntity> toEntityList(List<Product> domains);
}
