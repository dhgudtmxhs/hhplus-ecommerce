package kr.hhplus.be.server.coupon.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coupons")
@Tag(name = "Coupon API", description = "쿠폰 관련 API")
public class CouponController {

    @Operation(summary = "보유 쿠폰 조회", description = "사용자가 보유한 쿠폰 목록을 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<String> getUserCoupons(
            @Parameter(description = "사용자 ID", required = true)
            @PathVariable("userId") String userId) {

        String mockResponse = "["
                + "{\"couponId\": \"coupon123\", \"description\": \"10% 할인\", \"status\": \"VALID\"}"
                + "]";
        return ResponseEntity.ok(mockResponse);
    }

    @Operation(summary = "쿠폰 발급", description = "선착순으로 쿠폰을 발급합니다.")
    @PostMapping("/{userId}/issue")
    public ResponseEntity<String> issueCoupon(
            @Parameter(description = "사용자 ID", required = true)
            @PathVariable("userId") String userId) {

        boolean stockAvailable = true;

        if (stockAvailable) {
            // 쿠폰 발급 성공
            String mockResponse = "{\"message\": \"쿠폰 발급 완료\", \"couponId\": \"coupon124\"}";
            return ResponseEntity.ok(mockResponse);
        } else {
            // 쿠폰 재고 소진
            String errorResponse = "{\"message\": \"쿠폰 재고가 소진되었습니다.\"}";
            return ResponseEntity.status(400).body(errorResponse);
        }
    }
}