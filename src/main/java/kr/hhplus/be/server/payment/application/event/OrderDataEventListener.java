package kr.hhplus.be.server.payment.application.event;

import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.payment.infra.outbox.OrderDataKafkaPublisher;
import kr.hhplus.be.server.payment.infra.outbox.OrderDataOutbox;
import kr.hhplus.be.server.payment.infra.outbox.OrderOutboxJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderDataEventListener {

    private final OrderDataOutboxMapper orderDataOutboxMapper;
    private final OrderOutboxJPARepository orderOutboxRepository;
    private final OrderDataKafkaPublisher kafkaPublisher;

    // 트랜잭션 내에서 아웃박스 이벤트 저장
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saveOutbox(OrderDataEvent orderDataEvent) {

        OrderDataOutbox outbox = OrderDataOutbox.create(orderDataEvent);
        orderOutboxRepository.save(outbox);
    }

    // 트랜잭션 커밋 후 메시지 발행
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendOrderInfo(OrderDataEvent orderDataEvent) {

        OrderDataOutbox outbox = orderOutboxRepository.findByOrderId(orderDataEvent.orderData().orderId())
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.ORDER_DATA_OUTBOX_NOT_FOUND_CODE));

        OrderDataMessage message = orderDataOutboxMapper.toMessage(outbox);
        kafkaPublisher.publishOrderInfo(message);
    }
}
