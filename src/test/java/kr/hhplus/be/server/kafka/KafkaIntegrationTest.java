package kr.hhplus.be.server.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import org.junit.jupiter.api.BeforeEach;

@SpringBootTest(properties = {
        "spring.kafka.consumer.auto-offset-reset=earliest"
})
public class KafkaIntegrationTest {

    private static CountDownLatch latch;
    private static List<String> receivedMessages;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @BeforeEach
    public void setUp() {
        latch = new CountDownLatch(5);
        receivedMessages = new ArrayList<>();
    }

    // Kafka Consumer
    @KafkaListener(topics = "test-topic", groupId = "test-group")
    public void listen(String message) {
        receivedMessages.add(message);
        latch.countDown();
    }

    @Test
    public void 카프카_여러_메시지_송수신_테스트() throws Exception {
        List<String> messagesToSend = Arrays.asList(
                "message 1", "message 2", "message 3", "message 4", "message 5"
        );

        for(String message : messagesToSend) {
            kafkaTemplate.send("test-topic", message); // Producer
        }

        boolean allMessagesConsumed = latch.await(10, TimeUnit.SECONDS);
        assertTrue(allMessagesConsumed, "모든 메시지가 시간 내에 소비되지 않았습니다.");

        assertEquals(messagesToSend, receivedMessages, "수신된 메시지들이 전송한 메시지와 다릅니다.");
    }
}