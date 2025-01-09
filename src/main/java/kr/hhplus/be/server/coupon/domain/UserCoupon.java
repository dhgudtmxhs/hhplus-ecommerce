package kr.hhplus.be.server.coupon.domain;

public record UserCoupon(
        Long id,
        Long userId,
        Long couponId,
        Boolean isUsed
) {

    public UserCoupon markAsUsed() {
        if (this.isUsed) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }
        return new UserCoupon(this.id, this.userId, this.couponId, true);
    }
}
