package kr.hhplus.be.server.payment.infra.outbox;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.entity.BaseEntity;
import kr.hhplus.be.server.payment.application.event.OrderDataEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "order_data_outbox")
public class OrderDataOutbox extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private Long userId;

    private Long totalPrice;

    private String orderStatus;

    @Enumerated(EnumType.STRING)
    private OrderDataOutboxStatus orderDataOutboxStatus;

    public static OrderDataOutbox create(OrderDataEvent orderDataEvent) {
        OrderDataOutbox outbox = new OrderDataOutbox();
        outbox.orderId = orderDataEvent.orderData().orderId();
        outbox.userId = orderDataEvent.orderData().userId();
        outbox.totalPrice = orderDataEvent.orderData().totalPrice();
        outbox.orderStatus = orderDataEvent.orderData().status();
        outbox.orderDataOutboxStatus = OrderDataOutboxStatus.INIT;

        return outbox;
    }

    public void markPublished() {
        this.orderDataOutboxStatus = OrderDataOutboxStatus.PUBLISHED;
    }

}
