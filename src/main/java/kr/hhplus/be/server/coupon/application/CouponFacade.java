package kr.hhplus.be.server.coupon.application;

import kr.hhplus.be.server.coupon.domain.CouponService;
import kr.hhplus.be.server.coupon.domain.UserCoupon;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.domain.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponFacade {

    private final UserService userService;
    private final CouponService couponService;
    private final CouponInfoMapper couponInfoMapper;

    public List<UserCouponInfo> getUserCoupons(CouponCommand command) {

        User user = userService.getUser(command.userId());
        List<UserCoupon> userCoupons = couponService.getUserCoupons(user.getId());

        return couponInfoMapper.toUserCouponInfoList(userCoupons);
    }

    public UserCouponInfo issueCoupon(IssueCouponCommand command) {

        User user = userService.getUser(command.userId());
        UserCoupon issuedCoupon = couponService.issueCoupon(user.getId(), command.couponId());

        return couponInfoMapper.toUserCouponInfo(issuedCoupon);
    }
}
