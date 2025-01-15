package kr.hhplus.be.server.user;

import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.domain.UserRepository;
import kr.hhplus.be.server.user.domain.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 유효한_사용자_ID로_조회하면_유저를_반환한다() {
        // Given
        Long userId = 1L;
        User mockUser = new User(userId, "ohs");
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // When
        User result = userService.getUser(userId);

        // Then
        assertEquals(1, result.getId());
        assertEquals("ohs", result.getName());
        verify(userRepository).findById(userId);
    }

    @Test
    void 유효하지_않은_사용자_ID로_조회하면_IllegalArgumentException_예외가_발생한다() {
        // Given
        Long invalidUserId = -1L;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> userService.getUser(invalidUserId));
    }

    @Test
    void 존재하지_않는_사용자_ID_일_경우_NoSuchElementException_예외가_발생한다() {
        // Given
        Long nonExistentUserId = 100L;
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> userService.getUser(nonExistentUserId));
    }

}
