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
        coupon.issue();

        // Then
        assertEquals(6L, coupon.getIssuedCount());
    }

    @Test
    void 쿠폰_발급시_발급_횟수가_제한_수량과_같거나_많으면_IllegalStateException_예외가_발생한다() {
        // Given
        Coupon coupon1 = new Coupon(1L, "COUPON1", DiscountType.FIXED, 1000L, 5L, 5L); // 발급 횟수 == 제한 수량
        Coupon coupon2 = new Coupon(1L, "COUPON1", DiscountType.PERCENT, 10L, 5L, 6L); // 발급 횟수 > 제한 수량

        // When & Then
        assertThrows(IllegalStateException.class, coupon1::issue);
        assertThrows(IllegalStateException.class, coupon2::issue);
    }

}
