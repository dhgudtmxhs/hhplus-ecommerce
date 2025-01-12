package kr.hhplus.be.server.coupon;

import kr.hhplus.be.server.coupon.domain.Coupon;
import kr.hhplus.be.server.coupon.domain.DiscountType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CouponTest {

    @Test
    void 쿠폰_발급_및_횟수_증가가_정상적으로_수행된다() {
        // Given
        Coupon coupon = new Coupon(1L, "COUPON1", DiscountType.FIXED, 1000L, 10L, 5L);

        // When
        Coupon updatedCoupon = coupon.incrementIssuedCount();

        // Then
        assertEquals(6L, updatedCoupon.issuedCount());
    }

    @Test
    void 쿠폰_생성시_발급_횟수가_제한_수량과_같거나_많으면_IllegalStateException_예외가_발생한다() {
        // Given && When && Then
        assertThrows(IllegalStateException.class, () -> new Coupon(1L, "COUPON1",  DiscountType.FIXED,1000L, 5L, 5L));
        assertThrows(IllegalStateException.class, () -> new Coupon(1L, "COUPON1",  DiscountType.PERCENT,10L, 5L, 6L));
    }


}
