package kr.hhplus.be.server.payment.OrderDataEvent;

import kr.hhplus.be.server.payment.application.event.OrderDataMessage;
import kr.hhplus.be.server.payment.infra.outbox.OrderDataKafkaPublisher;
import kr.hhplus.be.server.payment.application.event.OrderDataEvent;
import kr.hhplus.be.server.payment.application.scheduler.OrderDataScheduler;
import kr.hhplus.be.server.payment.infra.outbox.OrderDataOutbox;
import kr.hhplus.be.server.payment.infra.outbox.OrderOutboxJPARepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// 카프카 consumer
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@EmbeddedKafka(partitions = 1, topics = { "order-data-topic" })
@SpringBootTest
class OrderDataEventSchedulerIntegrationTest {

    @Autowired
    private OrderDataScheduler orderDataScheduler;

    @Autowired
    private OrderOutboxJPARepository orderOutboxRepository;

    @Autowired
    private OrderDataKafkaPublisher kafkaPublisher;

    private CountDownLatch latch;
    private List<OrderDataMessage> receivedMessages;

    @BeforeEach
    void setUp() {
        latch = new CountDownLatch(1);
        receivedMessages = new ArrayList<>();
        orderOutboxRepository.deleteAll();
    }

    @KafkaListener( topics = "order-data-topic", groupId = "order-data-consumer-group-test2")
    public void consumeKafkaMessage(OrderDataMessage message) {
        receivedMessages.add(message);
        latch.countDown();
    }

    @Test
    void 주문_데이터_스케줄러_카프카_테스트() throws Exception {
        OrderDataOutbox outbox = OrderDataOutbox.create(
                new OrderDataEvent(new kr.hhplus.be.server.payment.application.OrderData(
                        10L, 100L, 10000L, "CREATED"))
        );
        orderOutboxRepository.save(outbox);

        setCreatedAt(outbox, LocalDateTime.now().minusMinutes(3));
        orderOutboxRepository.save(outbox);

        orderDataScheduler.rePublishOrderData();

        boolean allConsumed = latch.await(10, TimeUnit.SECONDS);

        assertThat(allConsumed).isTrue();

        OrderDataMessage msg = receivedMessages.get(0);
        assertThat(msg.orderId()).isEqualTo(10L);
        assertThat(msg.orderStatus()).isEqualTo("CREATED");
    }

    private void setCreatedAt(OrderDataOutbox outbox, LocalDateTime newTime) throws Exception {
        Field createdAtField = outbox.getClass().getSuperclass().getDeclaredField("createdAt");
        createdAtField.setAccessible(true);
        createdAtField.set(outbox, newTime);
    }
}