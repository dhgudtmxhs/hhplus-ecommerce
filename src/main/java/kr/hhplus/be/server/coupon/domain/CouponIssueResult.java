package kr.hhplus.be.server.coupon.domain;

import kr.hhplus.be.server.common.exception.ErrorCode;

public enum CouponIssueResult {
    COUPON_ALREADY_ISSUED("Coupon already issued") {
        @Override
        public void validateOrThrow() {
            throw new IllegalArgumentException(ErrorCode.COUPON_ALREADY_ISSUED_CODE);
        }
    },
    COUPON_ISSUED("Coupon issued") {
        @Override
        public void validateOrThrow() { // 정상 발급
        }
    },
    COUPON_ISSUE_CLOSED("Coupon issued closed") {
        @Override
        public void validateOrThrow() {
            throw new IllegalArgumentException(ErrorCode.COUPON_ISSUE_CLOSED_CODE);
        }
    },
    INTERNAL_ERROR("Internal error") {
        @Override
        public void validateOrThrow() {
            throw new IllegalArgumentException(ErrorCode.COUPON_ISSUE_FAILED_CODE);
        }
    };


    private final String value;

    CouponIssueResult(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }

    public static CouponIssueResult fromValue(String value) {
        for (CouponIssueResult result : values()) {
            if (result.getValue().equals(value)) {
                return result;
            }
        }
        return INTERNAL_ERROR;
    }

    public abstract void validateOrThrow();
}