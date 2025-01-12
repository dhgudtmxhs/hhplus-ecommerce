package kr.hhplus.be.server.point.interfaces;

import kr.hhplus.be.server.point.application.ChargePointCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PointRequestMapper {
    ChargePointCommand toChargePointCommand(ChargePointRequest request);
}
