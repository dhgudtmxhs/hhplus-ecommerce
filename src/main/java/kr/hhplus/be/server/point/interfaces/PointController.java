package kr.hhplus.be.server.point.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/point")
@Tag(name = "Point API", description = "잔액 충전 및 조회 API")
public class PointController {

    @Operation(summary = "포인트 조회", description = "사용자의 포인트를 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<String> getBalance(
            @Parameter(description = "사용자 ID", required = true)
            @PathVariable("userId") String userId) {

        String mockResponse = String.format("{\"userId\": \"%s\", \"point\": 10000}", userId);
        return ResponseEntity.ok(mockResponse);
    }

    @Operation(summary = "포인트 충전", description = "사용자의 포인트를 충전합니다.")
    @PatchMapping("/charge")
    public ResponseEntity<String> chargePoints(@RequestBody PointRequest request) {
        String userId = request.userId();
        Long amount = request.amount();

        String mockResponse = String.format(
                "{\"userId\": \"%s\", \"chargedAmount\": %d, \"newPoint\": 15000}",
                userId, amount
        );
        return ResponseEntity.ok(mockResponse);
    }


}
