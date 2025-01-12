package kr.hhplus.be.server.order.interfaces;

import kr.hhplus.be.server.order.application.OrderCommand;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderRequestMapper {

    OrderCommand toOrderCommand(OrderRequest orderRequest);
    ProductOrderRequest toProductOrderRequest(OrderRequest orderRequest);
}
