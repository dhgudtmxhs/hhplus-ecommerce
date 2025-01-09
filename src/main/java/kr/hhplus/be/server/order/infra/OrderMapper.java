package kr.hhplus.be.server.order.infra;

import kr.hhplus.be.server.coupon.infra.UserCouponEntity;
import kr.hhplus.be.server.order.domain.Order;
import kr.hhplus.be.server.product.domain.Product;
import kr.hhplus.be.server.product.infra.ProductEntity;
import kr.hhplus.be.server.user.infra.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    default OrderEntity toEntity(Order order) {
        return OrderEntity.builder()
                .user(UserEntity.builder().id(order.getUserId()).build())
                .userCoupon(order.getUserCouponId() != null ? UserCouponEntity.builder().id(order.getUserCouponId()).build() : null)
                .totalPrice(order.getTotalPrice())
                .finalPrice(order.getFinalPrice())
                .status(order.getStatus())
                .orderItems(order.getProducts().stream()
                        .map(product -> OrderItemEntity.builder()
                                .product(ProductEntity.builder().id(product.id()).build())
                                .quantity(product.stock())
                                .price(product.price())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    default Order toDomain(OrderEntity orderEntity) {
        return new Order(
                orderEntity.getId(),
                orderEntity.getUser().getId(),
                orderEntity.getTotalPrice(),
                orderEntity.getFinalPrice(),
                orderEntity.getUserCoupon() != null ? orderEntity.getUserCoupon().getId() : null,
                orderEntity.getStatus(),
                orderEntity.getOrderItems().stream()
                        .map(orderItem -> new Product(
                                orderItem.getProduct().getId(),
                                orderItem.getProduct().getName(),
                                orderItem.getPrice(),
                                orderItem.getQuantity()
                        ))
                        .collect(Collectors.toList())
        );
    }
}