package kr.hhplus.be.server.order.infra;

import kr.hhplus.be.server.coupon.infra.UserCouponEntity;
import kr.hhplus.be.server.order.domain.Order;
import kr.hhplus.be.server.order.domain.OrderItem;
import kr.hhplus.be.server.product.infra.ProductEntity;
import kr.hhplus.be.server.user.infra.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderEntity toEntity(Order order) {
        if (order == null) {
            return null;
        }

        // 1. OrderEntity 객체 생성
        OrderEntity orderEntity = OrderEntity.builder()
                .user(UserEntity.builder().id(order.getUserId()).build())
                .userCoupon(order.getUserCouponId() != null ? UserCouponEntity.builder().id(order.getUserCouponId()).build() : null)
                .totalPrice(order.getTotalPrice())
                .finalPrice(order.getFinalPrice())
                .status(order.getStatus())
                .build();

        // 2. OrderItemEntity 리스트 매핑 및 양방향 관계 설정
        List<OrderItemEntity> orderItemEntities = order.getOrderItems().stream()
                .map(orderItem -> toOrderItemEntity(orderItem, orderEntity))
                .collect(Collectors.toList());

        orderEntity.setOrderItems(orderItemEntities); // 연관 관계 설정

        return orderEntity;
    }

    public OrderItemEntity toOrderItemEntity(OrderItem orderItem, OrderEntity orderEntity) {
        if (orderItem == null) {
            return null;
        }

        // 수량과 가격을 정확하게 매핑하여 OrderItemEntity 생성
        return OrderItemEntity.builder()
                .product(ProductEntity.builder().id(orderItem.productId()).build())
                .quantity(orderItem.quantity())  // 수량 매핑
                .price(orderItem.price())        // 가격 매핑
                .order(orderEntity)
                .build();
    }

    public Order toDomain(OrderEntity orderEntity) {
        if (orderEntity == null) {
            return null;
        }

        List<OrderItem> orderItems = orderEntity.getOrderItems().stream()
                .map(orderItemEntity -> new OrderItem(
                        orderItemEntity.getId(),
                        orderItemEntity.getProduct().getId(),
                        orderItemEntity.getQuantity(),
                        orderItemEntity.getPrice()
                ))
                .collect(Collectors.toList());

        return new Order(
                orderEntity.getId(),
                orderEntity.getUser().getId(),
                orderEntity.getTotalPrice(),
                orderEntity.getFinalPrice(),
                orderEntity.getUserCoupon() != null ? orderEntity.getUserCoupon().getId() : null,
                orderEntity.getStatus(),
                orderItems
        );
    }
}