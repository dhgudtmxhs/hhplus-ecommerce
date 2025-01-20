package kr.hhplus.be.server.order.interfaces;

import kr.hhplus.be.server.order.application.OrderCommand;
import kr.hhplus.be.server.order.application.ProductOrderCommand;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderRequestMapper {

    default OrderCommand toOrderCommand(OrderRequest orderRequest) {
        if (orderRequest == null || orderRequest.products() == null) {
            throw new IllegalArgumentException("OrderRequest or products must not be null");
        }

        List<ProductOrderCommand> productOrderCommands = orderRequest.products().stream()
                .map(product -> new ProductOrderCommand(
                        product.productId(),
                        product.quantity()
                ))
                .toList();

        return new OrderCommand(orderRequest.userId(), productOrderCommands);
    }
}
