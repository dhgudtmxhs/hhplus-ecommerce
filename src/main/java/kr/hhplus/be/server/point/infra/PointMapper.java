package kr.hhplus.be.server.point.infra;

import kr.hhplus.be.server.point.domain.Point;
import kr.hhplus.be.server.user.infra.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PointMapper {

    @Mapping(target = "chargePoint", ignore = true)
    @Mapping(target = "deduct", ignore = true)
    @Mapping(target = "userId", source = "user.id")
    Point toDomain(PointEntity entity);

    default PointEntity toEntity(Point domain) {
        return PointEntity.builder()
                .id(domain.id()) // 기존 ID가 있는 경우 포함
                .user(UserEntity.builder().id(domain.userId()).build())
                .point(domain.point())
                .build();
    }
}

