package kr.hhplus.be.server.coupon.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.coupon.application.CouponCommand;
import kr.hhplus.be.server.coupon.application.CouponFacade;
import kr.hhplus.be.server.coupon.application.IssueCouponCommand;
import kr.hhplus.be.server.coupon.application.UserCouponInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Tag(name = "Coupon API", description = "쿠폰 관련 API")
public class CouponController {

    private final CouponFacade couponFacade;
    private final IssueCouponRequestMapper issueCouponRequestMapper;
    private final CouponResponseMapper couponResponseMapper;

    @Operation(summary = "보유 쿠폰 조회", description = "사용자가 보유한 쿠폰 목록을 조회합니다.")
    @GetMapping(value = {"/{userId}", "/"})
    public ResponseEntity<List<UserCouponResponse>> getUserCoupons(@Parameter(description = "사용자 ID", required = true)
                                                                   @PathVariable(value = "userId", required = false) Long userId) {

        CouponCommand command = new CouponCommand(userId);
        List<UserCouponInfo> userCoupons = couponFacade.getUserCoupons(command);

        List<UserCouponResponse> response = couponResponseMapper.toUserCouponResponseList(userCoupons);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "쿠폰 발급", description = "선착순으로 쿠폰을 발급합니다.")
    @PostMapping("/issue")
    public ResponseEntity<UserCouponResponse> issueCoupon(@Valid @RequestBody IssueCouponRequest request) {

        IssueCouponCommand command = issueCouponRequestMapper.toIssueCouponCommand(request);
        UserCouponInfo issueCouponInfo = couponFacade.issueCoupon(command);

        UserCouponResponse response = couponResponseMapper.toUserCouponResponse(issueCouponInfo);
        return ResponseEntity.ok(response);
    }
}