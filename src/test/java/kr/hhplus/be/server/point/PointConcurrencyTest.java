package kr.hhplus.be.server.point;

import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.point.domain.Point;
import kr.hhplus.be.server.point.domain.PointService;
import kr.hhplus.be.server.point.domain.PointRepository;
import kr.hhplus.be.server.user.infra.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PointConcurrencyTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private Long userId;

    @BeforeEach
    public void setup() {
        User user = userJpaRepository.save(User.builder()
                .name("ohs")
                .build());
        userId = user.getId();

        pointRepository.save(Point.builder()
                .userId(userId)
                .point(0L)
                .build());
    }

    @Test
    void 한_사용자가_동시에_포인트를_충전해도_포인트가_정상적으로_누적된다() throws InterruptedException, ExecutionException {
        int threadCount = 10;
        Long chargeAmountPerThread = 1000L;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            tasks.add(() -> {
                pointService.chargePoint(userId, chargeAmountPerThread);
                return null;
            });
        }

        CountDownLatch latch = new CountDownLatch(1);
        List<Future<Void>> futures = new ArrayList<>();

        for (Callable<Void> task : tasks) {
            futures.add(executorService.submit(() -> {
                latch.await();
                return task.call();
            }));
        }

        latch.countDown();

        for (Future<Void> future : futures) {
            future.get();
        }

        executorService.shutdown();

        Point point = pointService.getPoint(userId);
        Long expectedPoint = threadCount * chargeAmountPerThread;
        assertThat(point.getPoint()).isEqualTo(expectedPoint);
    }

    @Test
    void 한_사용자가_동시에_포인트를_충전해도_최대_포인트를_초과하지_않는다() throws InterruptedException, ExecutionException {
        int threadCount = 20;
        Long chargeAmountPerThread = 999_999L;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<Callable<Void>> tasks = new ArrayList<>();
        List<Throwable> exceptions = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            tasks.add(() -> {
                pointService.chargePoint(userId, chargeAmountPerThread);
                return null;
            });
        }

        CountDownLatch latch = new CountDownLatch(1);
        List<Future<Void>> futures = new ArrayList<>();

        for (Callable<Void> task : tasks) {
            futures.add(executorService.submit(() -> {
                latch.await();
                return task.call();
            }));
        }

        latch.countDown();

        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                exceptions.add(e.getCause());
            }
        }

        executorService.shutdown();

        Point point = pointService.getPoint(userId);
        Long finalPoint = point.getPoint();
        assertThat(finalPoint).isEqualTo(9_999_990L);
        assertThat(point.getPoint()).isLessThanOrEqualTo(Point.MAX_POINT);

        assertThat(exceptions).isNotEmpty();
        assertThat(exceptions.size()).isEqualTo(10);
    }
}
