package kr.hhplus.be.server.coupon.domain;

import kr.hhplus.be.server.common.exception.ErrorCode;

public record Coupon(
        Long id,
        String couponCode,
        DiscountType discountType, // 할인 유형 (정액 할인, 비율 할인)
        Long discountAmount,
        Long usageLimit,
        Long issuedCount
) {

    public Coupon incrementIssuedCount() {
        Long newIssuedCount = this.issuedCount + 1;
        return new Coupon(this.id, this.couponCode, this.discountType, this.discountAmount, this.usageLimit, newIssuedCount);
    }

    public void validateStock() {
        if (this.issuedCount >= this.usageLimit) {
            throw new IllegalStateException(ErrorCode.COUPON_STOCK_INSUFFICIENT_CODE);
        }
    }

}
