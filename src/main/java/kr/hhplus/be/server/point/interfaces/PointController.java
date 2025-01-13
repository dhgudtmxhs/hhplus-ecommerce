package kr.hhplus.be.server.point.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.point.application.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/point")
@RequiredArgsConstructor
@Tag(name = "Point API", description = "잔액 충전 및 조회 API")
public class PointController {

    private final PointFacade pointFacade;
    private final ChargePointRequestMapper chargePointRequestMapper;
    private final PointResponseMapper pointResponseMapper;

    @Operation(summary = "포인트 조회", description = "사용자의 포인트를 조회합니다.")
    @GetMapping(value = {"/{userId}", "/"})
    public ResponseEntity<PointResponse> getPoint(@Parameter(description = "사용자 ID", required = false)
                                                  @PathVariable(value = "userId", required = false) Long userId) {

        PointCommand command = new PointCommand(userId);
        PointInfo info = pointFacade.getPoint(command);
        PointResponse response = pointResponseMapper.toPointResponse(info);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "포인트 충전", description = "사용자의 포인트를 충전합니다.")
    @PatchMapping("/charge")
    public ResponseEntity<ChargePointResponse> chargePoints(@Valid @RequestBody ChargePointRequest request) {

        ChargePointCommand command = chargePointRequestMapper.toChargePointCommand(request);
        ChargePointInfo info = pointFacade.chargePoint(command);
        ChargePointResponse response = pointResponseMapper.toChargePointResponse(info);

        return ResponseEntity.ok(response);
    }


}
