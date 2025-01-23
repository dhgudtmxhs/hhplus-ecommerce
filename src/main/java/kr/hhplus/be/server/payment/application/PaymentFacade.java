package kr.hhplus.be.server.payment.application;

import kr.hhplus.be.server.coupon.domain.Coupon;
import kr.hhplus.be.server.coupon.domain.CouponService;
import kr.hhplus.be.server.order.domain.Order;
import kr.hhplus.be.server.order.domain.OrderService;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.domain.PaymentService;
import kr.hhplus.be.server.point.domain.Point;
import kr.hhplus.be.server.point.domain.PointService;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.domain.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentFacade {

    private final UserService userService;
    private final OrderService orderService;
    private final CouponService couponService;
    private final PointService pointService;
    private final PaymentService paymentService;
    private final OrderDataMapper orderDataMapper;
    private final PaymentInfoMapper paymentInfoMapper;
    private final ExternalDataService externalDataService;
    private final RedissonClient redissonClient;

    @Transactional
    public PaymentInfo createPayment(PaymentCommand command) {
        String lockKey = "lock:payment:order:" + command.orderId();
        RLock lock = redissonClient.getLock(lockKey);

        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(0, 5, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new IllegalStateException("동일 주문에 대해 동시에 여러 결제를 할 수 없습니다.");
            }

            userService.getUser(command.userId());

            Order order = orderService.getOrder(command.orderId());

            Long discountAmount = 0L;
            if (command.couponId() != null) {
                Coupon coupon = couponService.useCoupon(command.userId(), command.couponId());
                discountAmount = coupon.calculateDiscount(command.orderPrice());
            }

            Long finalPrice = command.orderPrice() - discountAmount;

            Point point = pointService.findPointForUpdate(command.userId());
            pointService.deductPoint(point, finalPrice);

            Payment payment = paymentService.createPayment(
                    command.orderId(),
                    command.orderPrice(),
                    finalPrice,
                    command.couponId()
            );

            orderService.completeOrder(command.orderId());

            OrderData orderData = orderDataMapper.toOrderData(order);
            externalDataService.sendOrderData(orderData);

            return paymentInfoMapper.toPaymentInfo(payment);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("결제 처리 중 interrupt 발생", e);
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}