package kr.hhplus.be.server.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    USER_ID_NULL("E001", "사용자 ID가 NULL 입니다."),
    USER_ID_INVALID("E002", "사용자 ID는 0보다 커야 합니다."),
    USER_NOT_FOUND("E003", "사용자를 찾을 수 없습니다."),

    POINT_CHARGE_AMOUNT_NULL("E004", "포인트 충전 금액이 NULL 입니다."),
    POINT_CHARGE_AMOUNT_INVALID("E005", "포인트 충전 금액은 0보다 커야 합니다."),
    POINT_NOT_FOUND("E006", "포인트를 찾을 수 없습니다."),
    POINT_MAX_EXCEED("E007", "포인트가 최대 잔액을 초과했습니다."),
    POINT_DEDUCT_EXCEED("E008", "차감할 포인트가 보유 포인트보다 많습니다."),

    PRODUCT_PRICE_INVALID("E009", "상품 가격은 0보다 커야 합니다."),
    PRODUCT_STOCK_INVALID("E010", "상품 재고는 0 이상이어야 합니다."),
    PRODUCT_QUANTITY_INVALID("E011", "상품 수량은 0보다 커야 합니다."),
    PRODUCT_STOCK_INSUFFICIENT("E012", "상품 재고가 부족합니다."),
    PRODUCT_NOT_FOUND("E013", "상품을 찾을 수 없습니다."),
    PRODUCTS_LIST_EMPTY("E014", "주문할 상품 목록이 비어 있습니다."),

    COUPON_ID_NULL("E015", "쿠폰 ID가 NULL 입니다."),
    COUPON_ID_INVALID("E016", "쿠폰 ID는 0보다 커야 합니다."),
    COUPON_CODE_NULL("E017", "쿠폰 코드가 NULL 입니다."),
    COUPON_ALREADY_USED_OR_NOT_FOUND("E018", "쿠폰이 이미 사용되었거나 존재하지 않습니다."),
    COUPON_STOCK_INSUFFICIENT("E019", "쿠폰 재고가 부족합니다."),
    COUPON_NOT_FOUND("E020", "쿠폰을 찾을 수 없습니다."),

    ORDER_FINAL_PRICE_INVALID("E021", "최종 결제 금액은 0원 이상이어야 합니다."),
    ORDER_STATUS_CHANGE_INVALID("E022", "결제 대기 상태에서만 상태 변경이 가능합니다."),
    ORDER_CANCEL_INVALID("E023", "결제 완료된 주문은 취소할 수 없습니다."),

    COUPON_ALREADY_ISSUED("E024", "해당 사용자에게 이미 발급된 쿠폰입니다."),
    PRODUCT_ID_NULL("E025", "상품 ID가 NULL 입니다."),
    PRODUCT_QUANTITY_NULL("E026", "상품 주문 수가 NULL 입니다."),
    ORDER_ID_NULL("E027", "주문 ID가 Null 입니다."),
    ORDER_ID_INVALID("E028", "주문 ID는 0보다 커야 합니다."),
    ORDER_PRICE_NULL("E029", "주문 금액이 Null 입니다."),
    ORDER_PRICE_INVALID("E030", "주문 금액은 0보다 커야 합니다."),
    ORDER_NOT_FOUND("E031", "주문을 찾을 수 없습니다."),
    PAY_PRICE_INVALID("E032", "결제금액은 음수일 수 없습니다."),
    ORDER_CONCURRENT_NOT_ALLOWED("E033", "동일 사용자는 동시에 여러 주문을 할 수 없습니다."),
    ORDER_DUPLICATE_PAYMENT("E034", "동일 주문에 대해 동시에 여러 결제를 할 수 없습니다."),
    ORDER_PROCESS_INTERRUPTED("E035", "주문 처리 중 쓰레드가 인터럽트되었습니다."),
    PAYMENT_PROCESS_INTERRUPTED("E036", "결제 처리 중 쓰레드가 인터럽트되었습니다."),
    ORDER_DATA_OUTBOX_NOT_FOUND("E037", "주문 정보 아웃박스 조회 결과가 NULL 입니다."),


    INTERNAL_SERVER_ERROR("E999", "예상치 못한 오류가 발생했습니다.");

    public static final String USER_ID_NULL_CODE = "E001";
    public static final String USER_ID_INVALID_CODE = "E002";
    public static final String USER_NOT_FOUND_CODE = "E003";

    public static final String POINT_CHARGE_AMOUNT_NULL_CODE = "E004";
    public static final String POINT_CHARGE_AMOUNT_INVALID_CODE = "E005";
    public static final String POINT_NOT_FOUND_CODE = "E006";
    public static final String POINT_MAX_EXCEED_CODE = "E007";
    public static final String POINT_DEDUCT_EXCEED_CODE = "E008";

    public static final String PRODUCT_PRICE_INVALID_CODE = "E009";
    public static final String PRODUCT_STOCK_INVALID_CODE = "E010";
    public static final String PRODUCT_QUANTITY_INVALID_CODE = "E011";
    public static final String PRODUCT_STOCK_INSUFFICIENT_CODE = "E012";
    public static final String PRODUCT_NOT_FOUND_CODE = "E013";
    public static final String PRODUCTS_LIST_EMPTY_CODE = "E014";

    public static final String COUPON_ID_NULL_CODE = "E015";
    public static final String COUPON_ID_INVALID_CODE = "E016";
    public static final String COUPON_CODE_NULL_CODE = "E017";
    public static final String COUPON_ALREADY_USED_OR_NOT_FOUND_CODE = "E018";
    public static final String COUPON_STOCK_INSUFFICIENT_CODE = "E019";
    public static final String COUPON_NOT_FOUND_CODE = "E020";

    public static final String ORDER_FINAL_PRICE_INVALID_CODE = "E021";
    public static final String ORDER_STATUS_CHANGE_INVALID_CODE = "E022";
    public static final String ORDER_CANCEL_INVALID_CODE = "E023";

    public static final String COUPON_ALREADY_ISSUED_CODE = "E024";
    public static final String PRODUCT_ID_NULL_CODE = "E025";
    public static final String PRODUCT_QUANTITY_NULL_CODE = "E026";
    public static final String ORDER_ID_NULL_CODE = "E027";
    public static final String ORDER_ID_INVALID_CODE = "E028";
    public static final String ORDER_PRICE_NULL_CODE = "E029";
    public static final String ORDER_PRICE_INVALID_CODE = "E030";
    public static final String ORDER_NOT_FOUND_CODE = "E031";
    public static final String PAY_PRICE_INVALID_CODE = "E032";
    public static final String ORDER_CONCURRENT_NOT_ALLOWED_CODE = "E033";
    public static final String ORDER_DUPLICATE_PAYMENT_CODE = "E034";
    public static final String ORDER_PROCESS_INTERRUPTED_CODE = "E035";
    public static final String PAYMENT_PROCESS_INTERRUPTED_CODE = "E036";
    public static final String ORDER_DATA_OUTBOX_NOT_FOUND_CODE = "E037";

    public static final String INTERNAL_SERVER_ERROR_CODE = "E999";

    private final String code;
    private final String message;

    // code -> ErrorCode 찾기
    private static final Map<String, ErrorCode> CODE_MAP = Arrays.stream(values()) // Enum 상수를 배열로 반환
            .collect(Collectors.toMap(ErrorCode::getCode, Function.identity())); // value(ErrorCode 객체)를 가져옴

    // code -> ErrorCode 반환
    public static ErrorCode fromCode(String code) {
        return CODE_MAP.getOrDefault(code, INTERNAL_SERVER_ERROR);
    }

}