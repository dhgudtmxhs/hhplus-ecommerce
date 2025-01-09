package kr.hhplus.be.server.user.infra;

import kr.hhplus.be.server.user.domain.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toDomain(UserEntity entity);
    UserEntity toEntity(User user);
}
