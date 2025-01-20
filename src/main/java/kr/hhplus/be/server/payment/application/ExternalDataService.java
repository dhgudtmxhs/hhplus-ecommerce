package kr.hhplus.be.server.payment.application;

import kr.hhplus.be.server.order.domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExternalDataService {

    // 외부 플랫폼 응답 반환
    public boolean sendOrderData(OrderData orderData) {

        log.info("주문 데이터 가짜 전송 - Order ID: {}", orderData.orderId());

        try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        return true;
    }

}
