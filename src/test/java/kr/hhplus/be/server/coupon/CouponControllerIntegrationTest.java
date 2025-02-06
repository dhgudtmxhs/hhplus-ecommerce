package kr.hhplus.be.server.coupon;

import kr.hhplus.be.server.common.redis.coupon.CouponEventInitializer;
import kr.hhplus.be.server.coupon.domain.Coupon;
import kr.hhplus.be.server.coupon.domain.DiscountType;
import kr.hhplus.be.server.coupon.infra.CouponJpaRepository;
import kr.hhplus.be.server.coupon.interfaces.IssueCouponRequest;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.infra.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CouponControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CouponEventInitializer couponEventInitializer;

    @Autowired
    private ObjectMapper objectMapper;

    private Long existingUserId;
    private Long couponId;

    @BeforeEach
    void setUp() {
        existingUserId = userJpaRepository.save(new User(null, "TestUser")).getId();
        Coupon coupon = couponJpaRepository.save(Coupon.builder()
                .couponCode("TEST_COUPON")
                .discountType(DiscountType.PERCENT)
                .discountAmount(10L)
                .usageLimit(5L)
                .issuedCount(0L)
                .build());
        couponId = coupon.getId();
        redisTemplate.getConnectionFactory().getConnection().flushDb();
        couponEventInitializer.initializeCouponStock(couponId, coupon.getUsageLimit().intValue(), Duration.ofHours(1));

    }

    @Test
    void 존재하는_사용자_ID로_보유_쿠폰을_조회한다() throws Exception {
        // Given
        Long userId = existingUserId;

        // When & Then
        mockMvc.perform(get("/api/coupons/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0)); // 예시로, 쿠폰 목록이 비어있다면 0으로 설정
    }

    @Test
    void 존재하는_사용자_ID로_쿠폰을_발급한다() throws Exception {
        // Given
        Long userId = existingUserId;
        IssueCouponRequest request = new IssueCouponRequest(userId, couponId);

        // When & Then
        mockMvc.perform(post("/api/coupons/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.couponId").value(couponId));
    }
}
