package kr.hhplus.be.server.product.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.product.application.ProductFacade;
import kr.hhplus.be.server.product.application.ProductInfo;
import kr.hhplus.be.server.product.application.ProductListInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLOutput;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product API", description = "상품 관련 API")
public class ProductController {

    private final ProductFacade productFacade;
    private final ProductResponseMapper productResponseMapper;

    @Operation(summary = "상품 목록 조회", description = "상품 목록을 페이지 단위로 조회합니다.")
    @GetMapping
    public ResponseEntity<ProductListResponse> getProducts(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                                           @RequestParam(name = "size", defaultValue = "10") Integer size) {
        ProductListInfo productListInfo = productFacade.getProducts(page, size);
        ProductListResponse response = productResponseMapper.toProductListResponse(productListInfo);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "인기 상품 조회", description = "최근 3일 기준 상위 5개의 인기 상품을 조회합니다.")
    @GetMapping("/popular")
    public ResponseEntity<List<ProductResponse>> getPopularProducts() {

        List<ProductInfo> popularProductInfos = productFacade.getPopularProducts();
        List<ProductResponse> responses = productResponseMapper.toProductResponseList(popularProductInfos);

        return ResponseEntity.ok(responses);
    }
}