package kr.hhplus.be.server.order.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.order.application.OrderCommand;
import kr.hhplus.be.server.order.application.OrderFacade;
import kr.hhplus.be.server.order.application.OrderInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order API", description = "주문 관련 API")
public class OrderController {

    private final OrderFacade orderFacade;
    private final OrderRequestMapper orderRequestMapper;
    private final OrderResponseMapper orderResponseMapper;

    @Operation(summary = "주문", description = "사용자의 주문을 처리합니다.")
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {

        OrderCommand orderCommand = orderRequestMapper.toOrderCommand(request);
        OrderInfo info = orderFacade.createOrder(orderCommand);

        OrderResponse response = orderResponseMapper.toOrderResponse(info);

        return ResponseEntity.ok(response);
    }
}
