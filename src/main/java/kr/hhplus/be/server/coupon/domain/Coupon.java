package kr.hhplus.be.server.coupon.domain;

public record Coupon(
        Long id,
        String couponCode,
        DiscountType discountType, // 할인 유형 (정액 할인, 비율 할인)
        Long discountAmount,
        Long usageLimit,
        Long issuedCount
) {

    public Coupon {
        validateStock(issuedCount, usageLimit);
    }

    public Coupon incrementIssuedCount() {
        Long newIssuedCount = this.issuedCount + 1;
        return new Coupon(this.id, this.couponCode, this.discountType, this.discountAmount, this.usageLimit, newIssuedCount);
    }

    private void validateStock(Long issuedCount, Long usageLimit) {
        if (issuedCount >= usageLimit) {
            throw new IllegalStateException("쿠폰 재고가 부족합니다.");
        }
    }



}
