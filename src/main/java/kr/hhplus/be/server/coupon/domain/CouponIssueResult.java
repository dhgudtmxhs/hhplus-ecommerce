package kr.hhplus.be.server.coupon.domain;

import lombok.Getter;

public enum CouponIssueResult {
    COUPON_ALREADY_ISSUED("Coupon already issued"),
    COUPON_ISSUED("Coupon issued"),
    COUPON_ISSUE_CLOSED("Coupon issued closed"),
    INTERNAL_ERROR("Internal error");

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
}