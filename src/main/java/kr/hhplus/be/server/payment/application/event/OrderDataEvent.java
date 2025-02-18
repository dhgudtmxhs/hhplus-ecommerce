package kr.hhplus.be.server.payment.application.event;

import kr.hhplus.be.server.payment.application.OrderData;

public record OrderDataEvent(
        OrderData orderData
){}
