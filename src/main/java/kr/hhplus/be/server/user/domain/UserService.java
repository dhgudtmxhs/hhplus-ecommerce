package kr.hhplus.be.server.user.domain;

import kr.hhplus.be.server.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUser(Long userId) {
        User.validateId(userId);

        return userRepository.findById(userId)
                .orElseThrow( () -> new NoSuchElementException(ErrorCode.USER_NOT_FOUND.getCode()) );
    }
}
