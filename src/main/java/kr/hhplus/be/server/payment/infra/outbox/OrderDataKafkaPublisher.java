package kr.hhplus.be.server.payment.infra.outbox;

import kr.hhplus.be.server.payment.application.event.OrderDataMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderDataKafkaPublisher {

    private final KafkaTemplate<String, OrderDataMessage> kafkaTemplate;

    public OrderDataKafkaPublisher(KafkaTemplate<String, OrderDataMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderInfo(OrderDataMessage message) {
        String key = String.valueOf(message.orderId());
        kafkaTemplate.send("order-data-topic", key, message);
    }
}