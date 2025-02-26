package kr.hhplus.be.server.payment.infra.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderOutboxJPARepository extends JpaRepository<OrderDataOutbox, Long> {

    Optional<OrderDataOutbox> findByOrderId(Long eventId);

    List<OrderDataOutbox> findAllByOrderDataOutboxStatusAndCreatedAtBetween(OrderDataOutboxStatus orderDataOutboxStatus, LocalDateTime thresholdTime, LocalDateTime now);
}
