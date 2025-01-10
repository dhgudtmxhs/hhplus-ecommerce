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

    @Mapping(target = "user", source = "userId")
    PointEntity toEntity(Point domain);

    default UserEntity map(Long userId) {
        return UserEntity.builder().id(userId).build();
    }

}