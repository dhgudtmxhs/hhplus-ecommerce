package kr.hhplus.be.server.order.domain;

import java.util.Optional;

public interface OrderRepository {
    Order saveOrder(Order order);
    void saveOrderItem(OrderItem item);

    Optional<Order> findById(Long orderId);
}
