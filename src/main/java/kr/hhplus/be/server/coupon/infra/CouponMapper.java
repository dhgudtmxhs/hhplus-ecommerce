package kr.hhplus.be.server.coupon.infra;

import kr.hhplus.be.server.coupon.domain.Coupon;
import kr.hhplus.be.server.coupon.domain.UserCoupon;
import kr.hhplus.be.server.user.infra.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CouponMapper {

    Coupon toCouponDomain(CouponEntity entity);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "couponId", source = "coupon.id")
    @Mapping(target = "isUsed", source = "isUsed")
    UserCoupon toUserCouponDomain(UserCouponEntity entity);

    List<UserCoupon> toUserCouponDomainList(List<UserCouponEntity> entities);

    CouponEntity toCouponEntity(Coupon domain);

    default UserCouponEntity toUserCouponEntity(UserCoupon domain) {
        return UserCouponEntity.builder()
                .user(UserEntity.builder().id(domain.userId()).build())
                .coupon(CouponEntity.builder().id(domain.couponId()).build())
                .isUsed(domain.isUsed())
                .build();
    }
}
