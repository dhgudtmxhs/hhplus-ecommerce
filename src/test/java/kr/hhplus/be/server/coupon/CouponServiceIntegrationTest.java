package kr.hhplus.be.server.coupon;

import kr.hhplus.be.server.coupon.domain.*;
import kr.hhplus.be.server.coupon.infra.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CouponServiceIntegrationTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    @Autowired
    private UserCouponJpaRepository userCouponJpaRepository;

    @BeforeEach
    void setUp() {
        // 초기화 작업이 필요하면 여기에 추가
    }

    @Test
    void 유효한_사용자_ID로_모든_사용_가능한_쿠폰을_조회한다() {
        // Given
        Long userId = 1L;

        // Coupon 저장
        Coupon coupon1 = new Coupon(null, "COUPON1", DiscountType.FIXED, 1000L, 10L, 5L);
        Coupon coupon2 = new Coupon(null, "COUPON2", DiscountType.PERCENT, 500L, 20L, 10L);
        couponJpaRepository.save(coupon1);
        couponJpaRepository.save(coupon2);

        // UserCoupon 저장 (userId와 couponId 직접 참조)
        UserCoupon userCoupon1 = new UserCoupon(null, userId, coupon1.getId(), false);
        UserCoupon userCoupon2 = new UserCoupon(null, userId, coupon2.getId(), false);
        userCouponJpaRepository.save(userCoupon1);
        userCouponJpaRepository.save(userCoupon2);

        // When
        List<UserCoupon> actualCoupons = couponService.getUserCoupons(userId);

        // Then
        assertNotNull(actualCoupons);
        assertEquals(2, actualCoupons.size(), "2개의 쿠폰이 조회되어야 함");
    }

    @Test
    void 유효한_사용자_ID와_쿠폰_코드로_쿠폰을_발급한다() {
        // Given
        Long userId = 1L;
        String couponCode = "COUPON1";

        Coupon coupon = new Coupon(null, couponCode, DiscountType.FIXED, 1000L, 10L, 5L);
        couponJpaRepository.save(coupon);

        // When
        UserCoupon issuedCoupon = couponService.issueCoupon(userId, couponCode);

        // Then
        assertNotNull(issuedCoupon);
        assertEquals(userId, issuedCoupon.getUserId(), "발급된 쿠폰의 사용자 ID가 일치해야 함");
        assertEquals(coupon.getId(), issuedCoupon.getCouponId(), "발급된 쿠폰의 쿠폰 ID가 일치해야 함");
    }

    @Test
    void 유효한_사용자_ID와_쿠폰_ID로_조회하면_쿠폰을_반환한다() {
        // Given
        Long userId = 1L;

        Coupon coupon = new Coupon(null, "COUPON1", DiscountType.FIXED, 1000L, 10L, 5L);
        couponJpaRepository.save(coupon);

        // UserCoupon 저장 (userId와 couponId 직접 참조)
        UserCoupon userCoupon = new UserCoupon(null, userId, coupon.getId(), false);
        userCouponJpaRepository.save(userCoupon);

        // When
        Coupon actualCoupon = couponService.getUserCoupon(userId, coupon.getId());

        // Then
        assertNotNull(actualCoupon);
        assertEquals(coupon.getId(), actualCoupon.getId(), "반환된 쿠폰의 ID가 일치해야 함");
    }
}