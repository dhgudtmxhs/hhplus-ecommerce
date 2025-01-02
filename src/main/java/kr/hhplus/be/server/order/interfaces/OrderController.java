package kr.hhplus.be.server.order.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order API", description = "주문/결제 관련 API")
public class OrderController {

    @Operation(summary = "주문/결제", description = "사용자의 주문과 결제를 처리합니다.")
    @PostMapping
    public ResponseEntity<String> createOrder(
            @RequestBody OrderRequest orderRequest) {

        boolean isCouponValid = true;
        boolean isStockAvailable = true;
        boolean isBalanceSufficient = true;

        if (!isCouponValid) {
            return ResponseEntity.status(400).body("{\"message\": \"쿠폰이 유효하지 않습니다.\"}");
        }

        if (!isStockAvailable || !isBalanceSufficient) {
            return ResponseEntity.status(400).body("{\"message\": \"잔액 부족 또는 재고 부족으로 주문에 실패했습니다.\"}");
        }

        // 주문 및 결제 성공
        return ResponseEntity.ok("{\"orderId\": \"order1234\", \"status\": \"SUCCESS\", \"totalAmount\": 2000}");
    }
}
