package kr.hhplus.be.server.order;

import kr.hhplus.be.server.order.application.OrderFacade;
import kr.hhplus.be.server.order.application.OrderCommand;
import kr.hhplus.be.server.order.application.OrderInfo;
import kr.hhplus.be.server.order.application.ProductOrderCommand;
import kr.hhplus.be.server.order.infra.OrderJpaRepository;
import kr.hhplus.be.server.product.domain.Product;
import kr.hhplus.be.server.product.infra.ProductJpaRepository;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.infra.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class OrderFacadeConcurrencyTest {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    private Long userId;
    private Long productId;
    private Long initialStock;

    @BeforeEach
    public void setup() {
        orderJpaRepository.deleteAll();
        productJpaRepository.deleteAll();
        userJpaRepository.deleteAll();

        initialStock = 100L;
        User user = userJpaRepository.save(User.builder()
                .name("testUser")
                .build());
        userId = user.getId();

        Product product = productJpaRepository.save(Product.builder()
                .name("Test Product")
                .stock(initialStock)
                .price(1000L)
                .build());
        productId = product.getId();
    }

    @Test
    void 여러_사용자가_동시에_주문을_요청해도_재고를_초과하지_않고_초과하면_주문에_실패한다() throws InterruptedException, ExecutionException {
        int threadCount = 10;
        int ordersPerThread = 10;
        long quantityPerOrder = 1L;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        Callable<Boolean> orderTask = () -> {
            for (int i = 0; i < ordersPerThread; i++) {
                OrderCommand command = new OrderCommand(
                        userId,
                        Collections.singletonList(new ProductOrderCommand(productId, quantityPerOrder))
                );
                try {
                    orderFacade.createOrder(command);
                } catch (Exception e) {
                    return false;
                }
            }
            return true;
        };

        List<Callable<Boolean>> tasks = Collections.nCopies(threadCount, orderTask);
        List<Future<Boolean>> results = executorService.invokeAll(tasks);
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        for (Future<Boolean> result : results) {
            assertThat(result.get()).isTrue();
        }

        Long finalStock = productJpaRepository.findById(productId)
                .map(Product::getStock)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
        assertThat(finalStock).isEqualTo(0L);
    }

    @Test
    void 여러_상품을_포함한_주문에서_하나라도_재고가_부족하면_재고를_초과하지_않고_주문에_실패한다() throws InterruptedException {
        int threadCount = 15; // 15개의 동시 요청
        long quantityPerOrder = 1L; // 각 상품당 주문 수량
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>();
        List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());

        productJpaRepository.deleteAll();
        Product product1 = productJpaRepository.save(Product.builder()
                .name("Product 1")
                .stock(5L) // 재고 5개
                .price(1000L)
                .build());
        Product product2 = productJpaRepository.save(Product.builder()
                .name("Product 2")
                .stock(10L) // 재고 10개
                .price(2000L)
                .build());
        Product product3 = productJpaRepository.save(Product.builder()
                .name("Product 3")
                .stock(15L) // 재고 15개
                .price(3000L)
                .build());

        for (int i = 0; i < threadCount; i++) {
            futures.add(executorService.submit(() -> {
                try {
                    latch.await();
                    OrderCommand command = new OrderCommand(
                            userId,
                            List.of(
                                    new ProductOrderCommand(product1.getId(), quantityPerOrder),
                                    new ProductOrderCommand(product2.getId(), quantityPerOrder),
                                    new ProductOrderCommand(product3.getId(), quantityPerOrder)
                            )
                    );
                    orderFacade.createOrder(command);
                    return true;
                } catch (Exception e) {
                    exceptions.add(e);
                    return false;
                }
            }));
        }

        latch.countDown();
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        long successCount = futures.stream()
                .map(result -> {
                    try {
                        return result.get();
                    } catch (Exception e) {
                        return false;
                    }
                })
                .filter(Boolean::booleanValue)
                .count();

        Long finalStock1 = productJpaRepository.findById(product1.getId())
                .map(Product::getStock)
                .orElseThrow(() -> new RuntimeException("Product 1을 찾을 수 없습니다."));
        Long finalStock2 = productJpaRepository.findById(product2.getId())
                .map(Product::getStock)
                .orElseThrow(() -> new RuntimeException("Product 2를 찾을 수 없습니다."));
        Long finalStock3 = productJpaRepository.findById(product3.getId())
                .map(Product::getStock)
                .orElseThrow(() -> new RuntimeException("Product 3을 찾을 수 없습니다."));

        assertThat(finalStock1).isGreaterThanOrEqualTo(0L);
        assertThat(finalStock2).isGreaterThanOrEqualTo(0L);
        assertThat(finalStock3).isGreaterThanOrEqualTo(0L);

        // 성공한 요청 수는 각 상품의 재고에서 총 감소량과 일치해야 함
        long expectedSuccessCount = 5;
        assertThat(successCount).isEqualTo(expectedSuccessCount);

        // 실패 요청 수는 총 요청 수 - 성공 요청 수
        assertThat(exceptions.size()).isEqualTo(threadCount - successCount);
    }
}