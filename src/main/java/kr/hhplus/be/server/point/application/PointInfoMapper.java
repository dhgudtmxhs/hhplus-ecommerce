package kr.hhplus.be.server.point.application;

import kr.hhplus.be.server.point.domain.Point;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PointInfoMapper {

    PointInfo toPointInfo(Point point);

    @Mapping(source = "point", target = "newPoint")
    ChargePointInfo toChargePointInfo(Point point);

}
