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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public PaymentInfo createPayment(PaymentCommand command) {
        log.info("Start Payment Process for User: {}, Order: {}", command.userId(), command.orderId());

        userService.getUser(command.userId());
        log.info("User validated: {}", command.userId());

        Order order = orderService.getOrder(command.orderId());
        log.info("Order validated: {}, Status: {}", command.orderId(), order.getStatus());

        Long discountAmount = 0L;
        if (command.couponId() != null) {
            log.info("Trying to use coupon for User: {}, Coupon: {}", command.userId(), command.couponId());
            Coupon coupon = couponService.useCoupon(command.userId(), command.couponId());
            discountAmount = coupon.calculateDiscount(command.orderPrice());
            log.info("Coupon used successfully. Discount: {}", discountAmount);
        }

        Long finalPrice = command.orderPrice() - discountAmount;
        log.info("Final price calculated: {}", finalPrice);

        Point point = pointService.findPointForUpdate(command.userId());
        log.info("Point deducted 전 테스트 for User: {}, Remaining Points: {}", command.userId(), point.getPoint());
        pointService.deductPoint(point, finalPrice);
        log.info("Point deducted for User: {}, Remaining Points: {}", command.userId(), point.getPoint());

        Payment payment = paymentService.createPayment(
                command.orderId(),
                command.orderPrice(),
                finalPrice,
                command.couponId()
        );
        log.info("Payment created: {}", payment.getId());

        orderService.completeOrder(command.orderId());
        log.info("Order completed: {}", command.orderId());

        OrderData orderData = orderDataMapper.toOrderData(order);
        externalDataService.sendOrderData(orderData);
        log.info("Order data sent to external service: {}", order.getId());

        return paymentInfoMapper.toPaymentInfo(payment);
    }
}
