package kr.hhplus.be.server.coupon;

import kr.hhplus.be.server.common.redis.coupon.RedisCouponService;
import kr.hhplus.be.server.coupon.domain.*;
import kr.hhplus.be.server.coupon.infra.CouponJpaRepository;
import kr.hhplus.be.server.coupon.infra.UserCouponJpaRepository;
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

    @Mock
    private UserCouponJpaRepository userCouponJpaRepository;

    @Mock
    private CouponJpaRepository couponJpaRepository;

    @Mock
    private RedisCouponService redisService;


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
    void 유효한_쿠폰_ID로_쿠폰_발급시_정상적으로_유저쿠폰을_반환한다() {
        // Given
        Long userId = 1L;
        Long couponId = 101L;

        Coupon coupon = new Coupon(couponId, "COUPON1", DiscountType.FIXED, 1000L, 10L, 5L);
        UserCoupon expectedUserCoupon = new UserCoupon(null, userId, couponId, false);

        when(redisService.requestCoupon(userId, couponId))
                .thenReturn(CouponIssueResult.COUPON_ISSUED);
        when(couponRepository.findCouponById(couponId))
                .thenReturn(Optional.of(coupon));

        // Mock 설정: 유저 쿠폰 저장
        when(couponRepository.saveUserCoupon(any(UserCoupon.class)))
                .thenReturn(expectedUserCoupon);

        // When
        UserCoupon actualUserCoupon = couponService.issueCoupon(userId, couponId);

        // Then
        assertEquals(expectedUserCoupon, actualUserCoupon);
        //verify(couponRepository).findByIdForUpdate(couponId);
        verify(couponRepository).findCouponById(couponId);
        //verify(couponRepository).findByCouponIdAndUserIdForUpdate(couponId, userId);
        verify(couponRepository).saveUserCoupon(any(UserCoupon.class));
    }


    @Test
    void 유효하지_않은_쿠폰_ID로_발급시_IllegalArgumentException_예외가_발생한다() {
        // Given
        Long userId = 1L;
        Long invalidCouponId = 999L;

        when(redisService.requestCoupon(userId, invalidCouponId)).thenReturn(CouponIssueResult.COUPON_ISSUED);

        when(couponRepository.findByIdForUpdate(invalidCouponId)).thenReturn(Optional.empty());

        // When && Then
        assertThrows(IllegalArgumentException.class, () -> couponService.issueCoupon(userId, invalidCouponId));
        //verify(couponRepository).findByIdForUpdate(invalidCouponId);
        verify(couponRepository).findCouponById(invalidCouponId);
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
        Coupon coupon = new Coupon(couponId, "TestCoupon", DiscountType.FIXED, 1000L, 10L, 5L);

        when(couponRepository.findByUserIdAndCouponIdAndIsUsedFalseForUpdate(userId, couponId))
                .thenReturn(Optional.of(userCoupon));
        when(couponRepository.findCouponById(couponId))
                .thenReturn(Optional.of(coupon));

        // When
        Coupon result = couponService.useCoupon(userId, couponId);

        // Then
        verify(couponRepository).saveUserCoupon(userCoupon);
        assertEquals(coupon, result);
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
