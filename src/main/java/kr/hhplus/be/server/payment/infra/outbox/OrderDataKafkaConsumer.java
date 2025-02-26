package kr.hhplus.be.server.payment.infra.outbox;

import kr.hhplus.be.server.payment.application.event.OrderDataMessage;
import kr.hhplus.be.server.payment.infra.outbox.OrderOutboxJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderDataKafkaConsumer {

    private final OrderOutboxJPARepository orderOutboxRepository;

    @KafkaListener(topics = "order-data-topic", groupId = "order-data-consumer-group")
    public void consume(OrderDataMessage orderCreatedMessage) {

        orderOutboxRepository.findByOrderId(orderCreatedMessage.orderId())
                .ifPresent(outbox -> {
                    outbox.markPublished();
                    orderOutboxRepository.save(outbox);
                });
    }
}