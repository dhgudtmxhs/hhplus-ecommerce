package kr.hhplus.be.server.user;

import static org.junit.jupiter.api.Assertions.*;

import kr.hhplus.be.server.user.domain.User;
import org.junit.jupiter.api.Test;

public class UserTest {

    @Test
    void 유효한_ID일_경우_검증시_예외가_발생하지_않는다() {
        // Given && when && then
        assertDoesNotThrow(() -> User.validateId(1L));
    }

    @Test
    void ID가_null이거나_0미만인_경우_IllegalArgumentException_예외가_발생한다() {
        // Given && when && then
        assertThrows(IllegalArgumentException.class, () -> User.validateId(null));
        assertThrows(IllegalArgumentException.class, () -> User.validateId(0L));
        assertThrows(IllegalArgumentException.class, () -> User.validateId(-1L));
    }


}
