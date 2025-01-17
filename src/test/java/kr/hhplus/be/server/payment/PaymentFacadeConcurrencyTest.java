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
import kr.hhplus.be.server.payment.application.PaymentCommand;
import kr.hhplus.be.server.payment.application.PaymentFacade;
import kr.hhplus.be.server.payment.application.PaymentInfo;
import kr.hhplus.be.server.point.domain.Point;
import kr.hhplus.be.server.point.infra.PointJpaRepository;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.infra.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PaymentFacadeConcurrencyTest {

    @Autowired
    private PaymentFacade paymentFacade;

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

    private User existingUser;
    private Order existingOrder;
    private Coupon existingCoupon;

    @BeforeEach
    public void setup() {
        userJpaRepository.deleteAll();
        orderJpaRepository.deleteAll();
        couponJpaRepository.deleteAll();
        userCouponJpaRepository.deleteAll();
        pointJpaRepository.deleteAll();
        // 유저 생성
        existingUser = userJpaRepository.save(
                User.builder()
                        .name("Test User")
                        .build()
        );

        // 주문 생성
        existingOrder = orderJpaRepository.save(
                Order.builder()
                        .userId(existingUser.getId())
                        .totalPrice(10000L)
                        .status(OrderStatus.CREATED)
                        .build()
        );

        // 쿠폰 생성
        existingCoupon = couponJpaRepository.save(
                Coupon.builder()
                        .couponCode("TEST-COUPON")
                        .discountType(DiscountType.FIXED)
                        .discountAmount(2000L)
                        .usageLimit(10L)
                        .issuedCount(0L)
                        .build()
        );

        // 유저-쿠폰 매핑 생성
        userCouponJpaRepository.save(
                UserCoupon.builder()
                        .userId(existingUser.getId())
                        .couponId(existingCoupon.getId())
                        .isUsed(false)
                        .build()
        );

        // 포인트 생성
        Point savedPoint = pointJpaRepository.save(
                Point.builder()
                        .userId(existingUser.getId())
                        .point(50000L) // 초기 포인트 설정
                        .build()
        );
        System.out.println("Saved Point: " + savedPoint);
    }

    @Test
    void 하나의_결제를_동시에_여러번_요청해도_1번만_성공한다() throws InterruptedException, ExecutionException {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(1);
        List<Callable<Boolean>> tasks = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            tasks.add(() -> {
                latch.await();
                try {
                    PaymentCommand command = new PaymentCommand(
                            existingUser.getId(),
                            existingOrder.getId(),
                            existingOrder.getTotalPrice(),
                            existingCoupon.getId()
                    );
                    paymentFacade.createPayment(command);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });
        }

        List<Future<Boolean>> results = new ArrayList<>();
        for (Callable<Boolean> task : tasks) {
            results.add(executorService.submit(task));
        }

        latch.countDown();
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        long successCount = results.stream().filter(result -> {
            try {
                return result.get();
            } catch (Exception e) {
                return false;
            }
        }).count();

        assertThat(successCount).isEqualTo(1);
    }
}
