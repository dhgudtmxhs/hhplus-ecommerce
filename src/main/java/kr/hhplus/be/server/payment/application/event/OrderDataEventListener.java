package kr.hhplus.be.server.payment.application.event;

import kr.hhplus.be.server.payment.application.ExternalDataService;
import kr.hhplus.be.server.payment.application.OrderData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderDataEventListener {

    private final ExternalDataService externalDataService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Retryable(
            value = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public void handleOrderDataEvent(OrderDataEvent event) {
        OrderData orderData = event.orderData();
        externalDataService.sendOrderData(orderData);
    }

    @Recover
    public void recoverTimeout(TimeoutException ex, OrderDataEvent event) {
        log.error("타임아웃 예외로 주문 데이터 전송 재시도 실패 - 주문 데이터: {}. 예외: {}",
                event.orderData(), ex.getMessage(), ex);
    }

    @Recover
    public void recover(Exception ex, OrderDataEvent event) {
        log.error("주문 데이터 전송 재시도 실패 - 주문 데이터: {}. 예외: {}",
                event.orderData(), ex.getMessage(), ex);
    }
}
