package kr.hhplus.be.server.coupon;

import kr.hhplus.be.server.coupon.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 유효한_사용자_ID로_조회하면_사용자의_쿠폰_목록을_반환한다() {
        // Given
        Long userId = 1L;
        List<UserCoupon> expectedUserCoupons = List.of(
                new UserCoupon(1L, userId, 101L, false),
                new UserCoupon(2L, userId, 102L, false)
        );

        when(couponRepository.findByUserIdAndIsUsedFalse(userId)).thenReturn(expectedUserCoupons);

        // When
        List<UserCoupon> actualUserCoupons = couponService.getUserCoupons(userId);

        // Then
        assertEquals(expectedUserCoupons, actualUserCoupons);
        verify(couponRepository).findByUserIdAndIsUsedFalse(userId);
    }

    @Test
    void 사용자_ID로_조회했을_때_사용자의_쿠폰이_없으면_빈_목록을_반환한다() {
        // Given
        Long userId = 1L;
        List<UserCoupon> emptyUserCoupons = List.of();

        when(couponRepository.findByUserIdAndIsUsedFalse(userId)).thenReturn(emptyUserCoupons);

        // When
        List<UserCoupon> actualUserCoupons = couponService.getUserCoupons(userId);

        // Then
        assertEquals(emptyUserCoupons, actualUserCoupons);
        verify(couponRepository).findByUserIdAndIsUsedFalse(userId);
    }

    @Test
    void 유효한_쿠폰코드로_쿠폰_발급시_정상적으로_유저쿠폰을_반환한다() {
        // Given
        Long userId = 1L;
        String couponCode = "COUPON1";
        Coupon coupon = new Coupon(1L, couponCode, DiscountType.FIXED, 1000L, 10L, 5L);
        UserCoupon expectedUserCoupon = new UserCoupon(null, userId, coupon.id(), false);
        Coupon updatedCoupon = coupon.incrementIssuedCount();

        when(couponRepository.findByCouponCodeForUpdate(couponCode)).thenReturn(Optional.of(coupon));
        when(couponRepository.saveUserCoupon(any(UserCoupon.class))).thenReturn(expectedUserCoupon);

        // When
        UserCoupon actualUserCoupon = couponService.issueCoupon(userId, couponCode);

        // Then
        assertEquals(expectedUserCoupon, actualUserCoupon);
        verify(couponRepository).findByCouponCodeForUpdate(couponCode);
        verify(couponRepository).saveCoupon(updatedCoupon);
        verify(couponRepository).saveUserCoupon(any(UserCoupon.class));
    }

    @Test
    void 유효하지_않은_쿠폰코드로_발급시_IllegalArgumentException_예외가_발생한다() {
        // Given
        Long userId = 1L;
        String invalidCouponCode = "INVALID_COUPON1";

        when(couponRepository.findByCouponCodeForUpdate(invalidCouponCode)).thenReturn(Optional.empty());

        // When && Then
        assertThrows(IllegalArgumentException.class, () -> couponService.issueCoupon(userId, invalidCouponCode));
        verify(couponRepository).findByCouponCodeForUpdate(invalidCouponCode);
        verify(couponRepository, never()).saveCoupon(any());
        verify(couponRepository, never()).saveUserCoupon(any());
    }

    @Test
    void 유효한_사용자_ID와_쿠폰_ID로_조회하면_쿠폰을_반환한다() {
        // Given
        Long userId = 1L;
        Long couponId = 101L;
        Coupon expectedCoupon = new Coupon(couponId, "COUPON1", DiscountType.FIXED, 1000L, 10L, 5L);

        when(couponRepository.findByUserIdAndCouponIdAndIsUsedFalse(userId, couponId))
                .thenReturn(Optional.of(expectedCoupon));

        // When
        Coupon actualCoupon = couponService.getUserCoupon(userId, couponId);

        // Then
        assertEquals(expectedCoupon, actualCoupon);
        verify(couponRepository).findByUserIdAndCouponIdAndIsUsedFalse(userId, couponId);
    }

    @Test
    void 유효하지_않은_사용자_ID나_쿠폰_ID로_조회하면_IllegalArgumentException_예외가_발생한다() {
        // Given
        Long userId = 1L;
        Long invalidCouponId = 999L;

        when(couponRepository.findByUserIdAndCouponIdAndIsUsedFalse(userId, invalidCouponId))
                .thenReturn(Optional.empty());

        // When && Then
        assertThrows(IllegalArgumentException.class, () -> couponService.getUserCoupon(userId, invalidCouponId));
        verify(couponRepository).findByUserIdAndCouponIdAndIsUsedFalse(userId, invalidCouponId);
    }

    @Test
    void 유효한_사용자_ID와_쿠폰_ID로_쿠폰을_사용하면_정상적으로_수행된다() {
        // Given
        Long userId = 1L;
        Long couponId = 101L;
        UserCoupon userCoupon = new UserCoupon(1L, userId, couponId, false);
        UserCoupon updatedUserCoupon = userCoupon.markAsUsed();

        when(couponRepository.findByUserIdAndCouponIdAndIsUsedFalseForUpdate(userId, couponId))
                .thenReturn(Optional.of(userCoupon));

        // When
        couponService.useCoupon(userId, couponId);

        // Then
        verify(couponRepository).saveUserCoupon(updatedUserCoupon);
    }

    @Test
    void 유효하지_않은_사용자_ID나_쿠폰_ID로_쿠폰을_사용하려_하면_IllegalArgumentException_예외가_발생한다() {
        // Given
        Long userId = 1L;
        Long invalidCouponId = 999L;

        when(couponRepository.findByUserIdAndCouponIdAndIsUsedFalseForUpdate(userId, invalidCouponId))
                .thenReturn(Optional.empty());

        // When && Then
        assertThrows(IllegalArgumentException.class, () -> couponService.useCoupon(userId, invalidCouponId));
        verify(couponRepository, never()).saveUserCoupon(any());
    }

}
