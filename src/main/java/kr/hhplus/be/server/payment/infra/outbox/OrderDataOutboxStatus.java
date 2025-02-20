package kr.hhplus.be.server.payment.infra.outbox;

public enum OrderDataOutboxStatus {
    INIT, // 초기상태
    PUBLISHED, // 메세지 발행 완료
    FAILED // 실패
}
