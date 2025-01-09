package kr.hhplus.be.server.coupon.infra;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.coupon.domain.Coupon;
import kr.hhplus.be.server.coupon.domain.CouponRepository;
import kr.hhplus.be.server.coupon.domain.UserCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;
    private final UserCouponJpaRepository userCouponJpaRepository;
    private final CouponMapper couponMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserCoupon> findByUserIdAndIsUsedFalse(Long userId) {
        List<UserCouponEntity> userCouponEntities = userCouponJpaRepository.findByUserIdAndIsUsedFalse(userId);
        return couponMapper.toUserCouponDomainList(userCouponEntities);
    }

    @Override
    public Optional<Coupon> findByCouponCodeForUpdate(String couponCode) {
        return couponJpaRepository.findByCouponCodeForUpdate(couponCode)
                .map(couponMapper::toCouponDomain);
    }

    @Override
    public void saveCoupon(Coupon updatedCoupon) {
        couponJpaRepository.save(couponMapper.toCouponEntity(updatedCoupon));
    }

    @Override
    public UserCoupon saveUserCoupon(UserCoupon userCoupon) {
        return couponMapper.toUserCouponDomain(
                userCouponJpaRepository.save(couponMapper.toUserCouponEntity(userCoupon))
        );
    }

    @Override
    public Optional<Coupon> findByUserIdAndCouponIdAndIsUsedFalse(Long userId, Long couponId) {
        QCouponEntity coupon = QCouponEntity.couponEntity;
        QUserCouponEntity userCoupon = QUserCouponEntity.userCouponEntity;

        CouponEntity result = queryFactory
                .select(coupon)
                .from(coupon)
                .join(userCoupon).on(userCoupon.coupon.id.eq(coupon.id))
                .where(userCoupon.user.id.eq(userId)
                        .and(userCoupon.coupon.id.eq(couponId))
                        .and(userCoupon.isUsed.isFalse()))
                .fetchOne();

        if (result == null) {
            return Optional.empty();
        }
        return Optional.of(couponMapper.toCouponDomain(result));
    }

    @Override
    public Optional<UserCoupon> findByUserIdAndCouponIdAndIsUsedFalseForUpdate(Long userId, Long couponId) {
        return userCouponJpaRepository.findByUserIdAndCouponIdAndIsUsedFalseForUpdate(userId, couponId)
                .map(couponMapper::toUserCouponDomain);
    }
}
