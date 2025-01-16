package kr.hhplus.be.server.order.domain;

import kr.hhplus.be.server.order.application.OrderItemData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order createOrder(Long userId, List<OrderItemData> orderItemDataList) {

        Long totalPrice = orderItemDataList.stream()
                .mapToLong(item -> item.price() * item.quantity())
                .sum();

        Order order = Order.builder()
                .userId(userId)
                .totalPrice(totalPrice)
                .status(OrderStatus.CREATED)
                .build();

        orderRepository.saveOrder(order);

        for (OrderItemData itemData : orderItemDataList) {
            OrderItem item = OrderItem.builder()
                    .orderId(order.getId())
                    .productId(itemData.productId())
                    .productName(itemData.productName())
                    .quantity(itemData.quantity())
                    .price(itemData.price() * itemData.quantity())
                    .build();
            orderRepository.saveOrderItem(item);
        }

        return order;
    }
}