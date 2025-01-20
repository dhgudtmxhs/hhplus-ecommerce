package kr.hhplus.be.server.payment.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.payment.application.PaymentCommand;
import kr.hhplus.be.server.payment.application.PaymentFacade;
import kr.hhplus.be.server.payment.application.PaymentInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment API", description = "결제 관련 API")
public class PaymentController {

    private final PaymentFacade paymentFacade;
    private final PaymentRequestMapper paymentRequestMapper;
    private final PaymentResponseMapper paymentResponseMapper;

    @Operation(summary = "결제 요청", description = "사용자의 결제 요청을 처리합니다.")
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentCommand command = paymentRequestMapper.toPaymentCommand(request);
        PaymentInfo paymentInfo = paymentFacade.createPayment(command);

        PaymentResponse response = paymentResponseMapper.toPaymentResponse(paymentInfo);
        return ResponseEntity.ok(response);
    }
}