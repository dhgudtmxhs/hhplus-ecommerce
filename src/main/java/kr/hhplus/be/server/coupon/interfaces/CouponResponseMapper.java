package kr.hhplus.be.server.coupon.interfaces;

import kr.hhplus.be.server.coupon.application.UserCouponInfo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CouponResponseMapper {
    UserCouponResponse toUserCouponResponse(UserCouponInfo info);
    List<UserCouponResponse> toUserCouponResponseList(List<UserCouponInfo> infos);
}
