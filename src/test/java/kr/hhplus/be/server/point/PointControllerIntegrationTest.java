package kr.hhplus.be.server.point;

import kr.hhplus.be.server.point.domain.Point;
import kr.hhplus.be.server.point.infra.PointJpaRepository;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.infra.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PointControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private PointJpaRepository pointJpaRepository;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User(null, "TestUser");
        existingUser = userJpaRepository.save(existingUser);

        Point point = Point.builder()
                .userId(existingUser.getId())
                .point(0L)
                .build();
        pointJpaRepository.save(point);
    }

    @Test
    void 존재하는_사용자의_ID로_포인트를_조회한다() throws Exception {
        // Given
        Long userId = existingUser.getId();

        // When & Then
        mockMvc.perform(get("/api/point/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.point").value(0L));
    }

    @Test
    void 존재하는_사용자의_ID로_포인트를_충전한다() throws Exception {
        // Given
        Long userId = existingUser.getId();
        Long amount = 500L;

        String chargeRequest = String.format("{\"userId\":%d,\"amount\":%d}", userId, amount);

        // When & Then
        mockMvc.perform(patch("/api/point/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(chargeRequest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.newPoint").value(500L));
    }
}
