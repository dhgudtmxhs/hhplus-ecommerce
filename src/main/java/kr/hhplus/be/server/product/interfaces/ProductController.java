package kr.hhplus.be.server.product.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product API", description = "상품 관련 API")
public class ProductController {

    @Operation(summary = "상품 조회", description = "특정 상품의 정보를 조회합니다.")
    @GetMapping("/{productId}")
    public ResponseEntity<String> getProductById(
            @Parameter(description = "조회할 상품의 ID", required = true)
            @PathVariable("productId") String productId) {

        if ("product001".equals(productId)) {
            String mockResponse = "{\"productId\": \"product001\", \"name\": \"상품 A\", \"price\": 5000, \"stock\": 50}";
            return ResponseEntity.ok(mockResponse);
        } else {
            String errorResponse = "{\"message\": \"상품을 찾을 수 없습니다.\"}";
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @Operation(summary = "인기 상품 조회", description = "최근 3일 기준 상위 5개의 인기 상품을 조회합니다.")
    @GetMapping("/popular")
    public ResponseEntity<String> getPopularProducts(
            @RequestParam(defaultValue = "3") int days,
            @RequestParam(defaultValue = "5") int limit) {

        String mockResponse = "["
                + "{\"productId\": \"product001\", \"name\": \"상품 A\", \"sales\": 100}, "
                + "{\"productId\": \"product003\", \"name\": \"상품 C\", \"sales\": 80}"
                + "]";
        return ResponseEntity.ok(mockResponse);
    }
}