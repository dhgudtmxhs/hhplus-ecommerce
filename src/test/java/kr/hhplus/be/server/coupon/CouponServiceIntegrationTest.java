package kr.hhplus.be.server.coupon;

import kr.hhplus.be.server.coupon.domain.*;
import kr.hhplus.be.server.coupon.infra.*;
import kr.hhplus.be.server.user.infra.UserEntity;
import kr.hhplus.be.server.user.infra.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CouponServiceIntegrationTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    CouponJpaRepository couponJpaRepository;

    @Autowired
    UserCouponJpaRepository userCouponJpaRepository;

    @Autowired
    UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void 유효한_사용자_ID로_모든_사용_가능한_쿠폰을_조회한다() {
        Long userId = 1L;

        // UserEntity 저장
        UserEntity userEntity = new UserEntity(null, "testUser");
        userJpaRepository.save(userEntity);

        // CouponEntity 저장
        CouponEntity couponEntity1 = new CouponEntity(null, "COUPON1", DiscountType.FIXED, 1000L, 10L, 5L);
        CouponEntity couponEntity2 = new CouponEntity(null, "COUPON2", DiscountType.PERCENT, 500L, 20L, 10L);
        couponJpaRepository.save(couponEntity1);
        couponJpaRepository.save(couponEntity2);

        // UserCouponEntity 저장 (UserEntity와 CouponEntity를 참조)
        UserCouponEntity userCouponEntity1 = new UserCouponEntity(null, userEntity, couponEntity1, false);
        UserCouponEntity userCouponEntity2 = new UserCouponEntity(null, userEntity, couponEntity2, false);
        userCouponJpaRepository.save(userCouponEntity1);
        userCouponJpaRepository.save(userCouponEntity2);

        // When
        List<UserCoupon> actualCoupons = couponService.getUserCoupons(userId);

        // Then
        assertNotNull(actualCoupons);
        assertEquals(2, actualCoupons.size()); // 2개의 쿠폰이 조회되어야 함
    }

    @Test
    void 유효한_사용자_ID와_쿠폰_코드로_쿠폰을_발급한다() {
        Long userId = 1L;
        String couponCode = "COUPON1";

        // UserEntity 저장
        UserEntity userEntity = new UserEntity(null, "testUser");
        userJpaRepository.save(userEntity);

        // CouponEntity 저장
        CouponEntity couponEntity = new CouponEntity(null, couponCode, DiscountType.FIXED, 1000L, 10L, 5L);
        couponJpaRepository.save(couponEntity);

        // When
        UserCoupon issuedCoupon = couponService.issueCoupon(userId, couponCode);

        // Then
        assertNotNull(issuedCoupon);
        assertEquals(userId, issuedCoupon.userId());
        assertEquals(couponEntity.getId(), issuedCoupon.userId());
    }

    @Test
    void 유효한_사용자_ID와_쿠폰_ID로_조회하면_쿠폰을_반환한다() {
        // Given
        Long userId = 1L;

        UserEntity userEntity = new UserEntity(null, "testUser");
        userJpaRepository.save(userEntity);

        CouponEntity couponEntity = new CouponEntity(null, "COUPON1", DiscountType.FIXED, 1000L, 10L, 5L);
        couponJpaRepository.save(couponEntity);

        // UserEntity와 CouponEntity를 참조
        UserCouponEntity userCouponEntity = new UserCouponEntity(null, userEntity, couponEntity, false);
        userCouponJpaRepository.save(userCouponEntity);

        // When
        Coupon actualCoupon = couponService.getUserCoupon(userId, couponEntity.getId());

        // Then
        assertNotNull(actualCoupon);
        assertEquals(couponEntity.getId(), actualCoupon.id());
    }


}
