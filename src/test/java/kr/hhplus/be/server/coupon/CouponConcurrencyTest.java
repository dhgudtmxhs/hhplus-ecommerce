package kr.hhplus.be.server.coupon;

import kr.hhplus.be.server.common.redis.coupon.CouponEventInitializer;
import kr.hhplus.be.server.coupon.domain.*;
import kr.hhplus.be.server.coupon.infra.CouponJpaRepository;
import kr.hhplus.be.server.coupon.infra.UserCouponJpaRepository;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.infra.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CouponConcurrencyTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    @Autowired
    private UserCouponJpaRepository userCouponJpaRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CouponEventInitializer couponEventInitializer;

    private Long couponId;
    private Long usageLimit;

    @BeforeEach
    public void setup() {
        couponJpaRepository.deleteAll();
        userCouponJpaRepository.deleteAll();

        usageLimit = 5L;
        Coupon coupon = couponJpaRepository.save(Coupon.builder()
                .couponCode("TEST_COUPON")
                .discountType(DiscountType.PERCENT)
                .discountAmount(10L)
                .usageLimit(usageLimit)
                .issuedCount(0L)
                .build());
        couponId = coupon.getId();
        redisTemplate.getConnectionFactory().getConnection().flushDb();
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());


        couponEventInitializer.initializeCouponStock(couponId, usageLimit.intValue(), Duration.ofHours(1));
    }

    @Test
    void 여러_사용자가_동시에_쿠폰을_발급해도_최대_사용_가능_수를_초과하지_않는다() throws InterruptedException {
        int threadCount = 150; // 동시 요청 수
        int maxCoupons = usageLimit.intValue(); // 최대 쿠폰 발급 수

        List<Long> userIds = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            User user = userJpaRepository.save(User.builder()
                    .name("testUser" + i)
                    .build());
            userIds.add(user.getId());
        }

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);

        List<Future<UserCoupon>> futures = new ArrayList<>();
        List<Throwable> exceptions = new ArrayList<>();

        for (Long userId : userIds) {
            futures.add(executorService.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();
                    return couponService.issueCoupon(userId, couponId);
                } catch (Exception e) {
                    synchronized (exceptions) {
                        exceptions.add(e);
                    }
                    return null;
                }
            }));
        }

        readyLatch.await();
        startLatch.countDown();

        for (Future<UserCoupon> future : futures) {
            try {
                future.get();
            } catch (ExecutionException | InterruptedException e) {
            }
        }

        executorService.shutdown();

        List<UserCoupon> allCoupons = userCouponJpaRepository.findAll();
        assertThat(allCoupons).hasSize(maxCoupons);

        assertThat(exceptions).hasSize(threadCount - maxCoupons);
    }

    @Test
    void 동일_사용자가_동일_쿠폰을_여러_번_요청해도_중복_발급되지_않는다() throws InterruptedException {
        int requestCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(requestCount);
        List<Throwable> exceptions = new ArrayList<>();
        List<Future<UserCoupon>> futures = new ArrayList<>();

        CountDownLatch latch = new CountDownLatch(1);

        User user = userJpaRepository.save(User.builder()
                .name("testUser")
                .build());
        Long userId = user.getId();

        for (int i = 0; i < requestCount; i++) {
            futures.add(executorService.submit(() -> {
                latch.await();
                try {
                    return couponService.issueCoupon(userId, couponId);
                } catch (Exception e) {
                    synchronized (exceptions) {
                        exceptions.add(e);
                    }
                    return null;
                }
            }));
        }

        latch.countDown();

        for (Future<UserCoupon> future : futures) {
            try {
                future.get();
            } catch (ExecutionException | InterruptedException e) {
            }
        }

        executorService.shutdown();

        List<UserCoupon> userCoupons = userCouponJpaRepository.findByUserIdAndIsUsedFalse(userId);

        assertThat(userCoupons).hasSize(1);
        UserCoupon userCoupon = userCoupons.get(0);
        assertThat(userCoupon.getIsUsed()).isFalse();

        assertThat(exceptions).hasSize(requestCount - 1);
        for (Throwable exception : exceptions) {
            assertThat(exception)
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

}
