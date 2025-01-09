package kr.hhplus.be.server.point.interfaces;

import kr.hhplus.be.server.point.application.ChargePointInfo;
import kr.hhplus.be.server.point.application.PointInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PointResponseMapper {
    PointResponse toPointResponse(PointInfo pointInfo);
    ChargePointResponse toChargePointResponse(ChargePointInfo chargePointInfo);
}