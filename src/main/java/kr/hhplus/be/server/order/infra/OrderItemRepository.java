package kr.hhplus.be.server.order.infra;

import kr.hhplus.be.server.order.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
