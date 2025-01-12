package kr.hhplus.be.server.coupon.interfaces;

import kr.hhplus.be.server.coupon.application.IssueCouponCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IssueCouponRequestMapper {
    IssueCouponCommand toIssueCouponCommand(IssueCouponRequest couponRequest);
}
