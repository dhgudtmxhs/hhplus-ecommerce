package kr.hhplus.be.server.order.domain;

import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.order.application.OrderItemData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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

    @Transactional(readOnly = true)
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.ORDER_NOT_FOUND_CODE));
    }

    public void completeOrder(Long orderId) {
        Order order = getOrder(orderId);

        order.markAsCompleted();

        orderRepository.saveOrder(order);
    }
}