package kr.hhplus.be.server.user;

import static org.junit.jupiter.api.Assertions.*;

import kr.hhplus.be.server.user.domain.User;
import org.junit.jupiter.api.Test;

public class UserTest {

    @Test
    void 유효한_ID와_이름일_경우_객체가_정상적으로_생성된다() {
        // Given && when && then
        assertDoesNotThrow(() -> new User(1L, "ohs"));
    }

    @Test
    void ID가_null이거나_0이하인_경우_IllegalArgumentException_예외가_발생한다() {
        // Given && when && then
        assertThrows(IllegalArgumentException.class, () -> User.validateId(null));
        assertThrows(IllegalArgumentException.class, () -> User.validateId(0L));
        assertThrows(IllegalArgumentException.class, () -> User.validateId(-1L));
    }
}