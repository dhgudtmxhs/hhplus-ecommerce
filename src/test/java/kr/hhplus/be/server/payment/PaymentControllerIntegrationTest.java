package kr.hhplus.be.server.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.coupon.domain.Coupon;
import kr.hhplus.be.server.coupon.domain.DiscountType;
import kr.hhplus.be.server.coupon.domain.UserCoupon;
import kr.hhplus.be.server.coupon.infra.CouponJpaRepository;
import kr.hhplus.be.server.coupon.infra.UserCouponJpaRepository;
import kr.hhplus.be.server.order.domain.Order;
import kr.hhplus.be.server.order.domain.OrderStatus;
import kr.hhplus.be.server.order.infra.OrderJpaRepository;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.domain.PaymentRepository;
import kr.hhplus.be.server.payment.infra.PaymentJpaRepository;
import kr.hhplus.be.server.payment.interfaces.PaymentRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    @Autowired
    private UserCouponJpaRepository userCouponJpaRepository;

    @Autowired
    private PointJpaRepository pointJpaRepository;

    @Autowired
    private PaymentJpaRepository paymentJpaRepository;

    private User existingUser;
    private Order existingOrder;
    private Coupon existingCoupon;

    @BeforeEach
    void setUp() {
        // 데이터 초기화
        userJpaRepository.deleteAll();
        orderJpaRepository.deleteAll();
        couponJpaRepository.deleteAll();
        paymentJpaRepository.deleteAll();

        // 유저 데이터 생성 및 저장
        existingUser = User.builder()
                .name("Test User")
                .build();
        existingUser = userJpaRepository.save(existingUser);

        // 주문 데이터 생성 및 저장
        existingOrder = Order.builder()
                .userId(existingUser.getId())
                .totalPrice(10000L)
                .status(OrderStatus.CREATED)
                .build();
        existingOrder = orderJpaRepository.save(existingOrder);

        // 쿠폰 데이터 생성 및 저장
        existingCoupon = Coupon.builder()
                .couponCode("TEST-COUPON")
                .discountType(DiscountType.FIXED)
                .discountAmount(2000L)
                .usageLimit(10L)
                .issuedCount(0L)
                .build();
        existingCoupon = couponJpaRepository.save(existingCoupon);

        // 유저-쿠폰 데이터 생성 및 저장
        UserCoupon userCoupon = UserCoupon.builder()
                .userId(existingUser.getId())
                .couponId(existingCoupon.getId())
                .isUsed(false) // 사용되지 않은 상태로 설정
                .build();
        userCouponJpaRepository.save(userCoupon);

        // 포인트 데이터 생성 및 저장
        Point point = Point.builder()
                .userId(existingUser.getId())
                .point(15000L) // 충분한 포인트 설정
                .build();
        pointJpaRepository.save(point);
    }

    @Test
    void 유효한_결제_요청을_처리하고_결제정보를_반환한다() throws Exception {
        // Given
        PaymentRequest request = new PaymentRequest(
                existingUser.getId(),
                existingOrder.getId(),
                existingOrder.getTotalPrice(),
                existingCoupon.getId()
        );

        // When & Then
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(existingOrder.getId()))
                .andExpect(jsonPath("$.orderPrice").value(existingOrder.getTotalPrice()))
                .andExpect(jsonPath("$.finalPrice").value(8000L)) // 할인 적용된 금액
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void 존재하지_않는_쿠폰_ID로_결제_요청시_에러가_발생한다() throws Exception {
        // Given
        PaymentRequest request = new PaymentRequest(
                existingUser.getId(),
                existingOrder.getId(),
                existingOrder.getTotalPrice(),
                9999L // 존재하지 않는 쿠폰 ID
        );

        // When & Then
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("E018")); // COUPON_NOT_FOUND_CODE
    }
}