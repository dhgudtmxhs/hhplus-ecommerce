package kr.hhplus.be.server.payment.application.scheduler;

import kr.hhplus.be.server.payment.application.event.OrderDataOutboxMapper;
import kr.hhplus.be.server.payment.infra.outbox.OrderDataKafkaPublisher;
import kr.hhplus.be.server.payment.infra.outbox.OrderDataOutbox;
import kr.hhplus.be.server.payment.infra.outbox.OrderDataOutboxStatus;
import kr.hhplus.be.server.payment.infra.outbox.OrderOutboxJPARepository;
import kr.hhplus.be.server.payment.application.event.OrderDataMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderDataScheduler {

    private final OrderOutboxJPARepository orderOutboxRepository;
    private final OrderDataKafkaPublisher kafkaPublisher;
    private final OrderDataOutboxMapper orderDataOutboxMapper;

    @Scheduled(fixedRate = 60000)
    public void rePublishOrderData() {
        LocalDateTime thresholdTime = LocalDateTime.now().minusMinutes(5);
        LocalDateTime now = LocalDateTime.now();

        List<OrderDataOutbox> pendingEvents = orderOutboxRepository.findAllByOrderDataOutboxStatusAndCreatedAtBetween(
                OrderDataOutboxStatus.INIT, thresholdTime, now
        );

        for (OrderDataOutbox event : pendingEvents) {
            try {
                OrderDataMessage message = orderDataOutboxMapper.toMessage(event);
                kafkaPublisher.publishOrderInfo(message);

                event.markPublished();
                orderOutboxRepository.save(event);
                log.info("메시지 재발행 성공: orderId={}", event.getOrderId());

            } catch (Exception e) {
                orderOutboxRepository.save(event);
                log.warn("메시지 재발행 실패: orderId={}", event.getOrderId(), e);
            }
        }
    }
}
