package kr.hhplus.be.server.user.domain;

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
                .orElseThrow(() -> new NoSuchElementException("유저가 없습니다. 유저아이디 : " + userId));
    }
}
