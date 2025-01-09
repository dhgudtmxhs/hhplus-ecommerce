package kr.hhplus.be.server.order.domain;

public enum OrderStatus {
    PENDING,     // 주문 생성, 결제 대기 중
    PAID,        // 결제 완료 (배송 준비 중)
    FAILED,      // 결제 실패
    CANCELLED    // 주문 취소
    //SHIPPING    // 배송 중
    //DELIVERED   // 배송 완료
}