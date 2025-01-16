package kr.hhplus.be.server.order.domain;

public interface OrderRepository {
    Order saveOrder(Order order);
    void saveOrderItem(OrderItem item);
}
