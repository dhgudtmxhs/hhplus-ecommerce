package kr.hhplus.be.server.order.domain;

public enum OrderStatus {
    CREATED,      // 주문 생성됨, 결제 대기 중
    COMPLETED,    // 결제 완료됨, 주문 확정
    CANCELLED     // 주문 취소됨
}