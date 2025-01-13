package kr.hhplus.be.server.point;

import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.point.interfaces.PointController;
import kr.hhplus.be.server.user.infra.UserEntity;
import kr.hhplus.be.server.user.infra.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PointControllerLoggingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    void 존재하지_않는_사용자_ID로_포인트_조회하면_NoSuchElementException_예외가_발생하고_로그가_찍힌다() throws Exception {
        // Given
        Long nonExistentUserId = 1000L;

        // When
        mockMvc.perform(get("/api/point/{userId}", nonExistentUserId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()));

    }

    @Test
    void 음수_금액으로_포인트_충전하면_valid_예외가_발생하고_로그가_찍힌다() throws Exception {
        // Given
        Long invalidAmount = -500L;
        UserEntity existingUser = new UserEntity(null, "TestUser");
        userJpaRepository.save(existingUser);

        // When & Then
        mockMvc.perform(patch("/api/point/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"userId\": %d, \"amount\": %d}", existingUser.getId(), invalidAmount))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.POINT_CHARGE_AMOUNT_INVALID.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.POINT_CHARGE_AMOUNT_INVALID.getMessage()));

    }

    @Test
    void 사용자_ID가_null_일때_포인트_충전하면_valid_예외가_발생하고_로그가_찍힌다() throws Exception {
        // Given
        Long nullUserId = null;
        Long validAmount = 500L;

        // When & Then
        mockMvc.perform(patch("/api/point/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"userId\": %d, \"amount\": %d}", nullUserId, validAmount))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_ID_NULL.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_ID_NULL.getMessage()));
    }

    @Test
    void 사용자_ID가_null_일때_포인트_조회하면_valid_예외가_발생하고_로그가_찍힌다() throws Exception {
        // Given
        Long nullUserId = null;

        // When & Then
        mockMvc.perform(get("/api/point/{userId}", nullUserId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_ID_NULL.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_ID_NULL.getMessage()));
    }

}
