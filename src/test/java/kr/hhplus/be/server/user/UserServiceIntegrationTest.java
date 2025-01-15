package kr.hhplus.be.server.user;

import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.domain.UserService;
import kr.hhplus.be.server.user.infra.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    void 유효한_사용자_ID로_조회하면_유저를_반환한다() {
        // Given
        User savedEntity = userJpaRepository.save(new User(null, "ohs"));

        // When
        User result = userService.getUser(savedEntity.getId());

        // Then
        assertEquals(savedEntity.getId(), result.getId());
        assertEquals(savedEntity.getName(), result.getName());
    }

}

