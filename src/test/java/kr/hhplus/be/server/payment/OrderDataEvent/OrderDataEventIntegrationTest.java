package kr.hhplus.be.server.payment.OrderDataEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import kr.hhplus.be.server.payment.application.OrderData;
import kr.hhplus.be.server.payment.application.event.OrderDataEvent;
import kr.hhplus.be.server.payment.application.event.OrderDataMessage;
import kr.hhplus.be.server.payment.infra.outbox.OrderDataOutbox;
import kr.hhplus.be.server.payment.infra.outbox.OrderOutboxJPARepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.support.TransactionTemplate;

@EmbeddedKafka(partitions = 1, topics = { "order-data-topic" })
@SpringBootTest
public class OrderDataEventIntegrationTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private OrderOutboxJPARepository orderOutboxRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private CountDownLatch latch;
    private String receivedMessage;

    @BeforeEach
    public void setUp() {
        latch = new CountDownLatch(1);
        receivedMessage = null;
    }

    @KafkaListener(topics = "order-data-topic", groupId = "order-data-consumer-group-test1")
    public void listen(OrderDataMessage message) {
        receivedMessage = message.toString();
        latch.countDown();
    }

    @AfterEach
    public void tearDown() {
        // KafkaListener는 컨텍스트 종료 시 자동 정리됩니다.
    }

    @Test
    public void 주문_정보_카프카_송수신_테스트() throws Exception {
        transactionTemplate.execute(status -> {
            OrderData orderData = new OrderData(1L, 100L, 10000L, "CREATED");
            OrderDataEvent event = new OrderDataEvent(orderData);
            eventPublisher.publishEvent(event);
            return null;
        });

        boolean messageReceived = latch.await(10, TimeUnit.SECONDS);
        assertTrue(messageReceived, "Kafka 메시지가 지정 시간 내에 수신되지 않았습니다.");

        OrderDataOutbox outbox = orderOutboxRepository.findByOrderId(1L)
                .orElseThrow(() -> new IllegalStateException("OrderDataOutbox 엔티티가 존재하지 않습니다."));
        assertThat(outbox).isNotNull();

        String expectedSubstring = "orderId=1";
        assertThat(receivedMessage).contains(expectedSubstring);
    }
}