package kr.hhplus.be.server.point.infra;

import kr.hhplus.be.server.point.domain.Point;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PointMapper {

    @Mapping(target = "chargePoint", ignore = true)
    @Mapping(target = "deduct", ignore = true)
    Point toDomain(PointEntity entity);

    PointEntity toEntity(Point domain);
}