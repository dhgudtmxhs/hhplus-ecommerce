package kr.hhplus.be.server.coupon.application;

import kr.hhplus.be.server.coupon.domain.UserCoupon;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CouponInfoMapper {
    UserCouponInfo toUserCouponInfo(UserCoupon coupon);
    List<UserCouponInfo> toUserCouponInfoList(List<UserCoupon> coupons);
}
