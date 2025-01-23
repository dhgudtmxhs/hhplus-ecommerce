package kr.hhplus.be.server.order.application;

import kr.hhplus.be.server.order.domain.Order;
import kr.hhplus.be.server.order.domain.OrderService;
import kr.hhplus.be.server.product.domain.ProductService;
import kr.hhplus.be.server.user.domain.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderFacade {

    private final UserService userService;
    private final OrderService orderService;
    private final ProductService productService;
    private final OrderItemDataMapper orderItemDataMapper;
    private final OrderInfoMapper orderInfoMapper;
    private final RedissonClient redissonClient;

    @Transactional
    public OrderInfo createOrder(OrderCommand command) {
        String lockKey = "lock:user:" + command.userId();
        RLock lock = redissonClient.getLock(lockKey);

        boolean isLocked = false;
        try {

            isLocked = lock.tryLock(0, 5, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new IllegalStateException("동일 사용자는 동시에 여러 주문을 할 수 없습니다.");
            }

            userService.getUser(command.userId());

            Map<Long, Long> productQuantities = command.productOrderCommands().stream()
                    .collect(Collectors.toMap(ProductOrderCommand::productId, ProductOrderCommand::quantity));

            List<Long> productIds = List.copyOf(productQuantities.keySet());

            List<OrderItemData> orderItemDataList = productService.findByIdForUpdate(productIds).stream()
                    .map(product -> orderItemDataMapper.toOrderItemData(
                            product,
                            productQuantities.get(product.getId())
                    ))
                    .collect(Collectors.toList());

            productService.deductStock(productQuantities);

            Order order = orderService.createOrder(command.userId(), orderItemDataList);

            List<OrderItemInfo> orderItemInfos = orderItemDataList.stream()
                    .map(orderInfoMapper::toOrderItemInfo)
                    .collect(Collectors.toList());
            return orderInfoMapper.toOrderInfo(order, orderItemInfos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("주문 처리 중 interruptr 발생", e);
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
