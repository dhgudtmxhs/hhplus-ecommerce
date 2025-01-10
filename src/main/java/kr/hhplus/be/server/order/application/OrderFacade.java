package kr.hhplus.be.server.order.application;


import kr.hhplus.be.server.coupon.domain.Coupon;
import kr.hhplus.be.server.coupon.domain.CouponService;
import kr.hhplus.be.server.order.domain.Order;
import kr.hhplus.be.server.order.domain.OrderService;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.domain.PaymentService;
import kr.hhplus.be.server.payment.domain.PaymentStatus;
import kr.hhplus.be.server.point.domain.Point;
import kr.hhplus.be.server.point.domain.PointService;
import kr.hhplus.be.server.product.domain.Product;
import kr.hhplus.be.server.product.domain.ProductService;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.domain.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final UserService userService;
    private final PointService pointService;
    private final CouponService couponService;
    private final OrderService orderService;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final ExternalDataService externalDataService;

    public OrderInfo createOrder(OrderCommand command) {

        // 1. 사용자 조회
        User user = userService.getUser(command.userId());

        // 2. 포인트 조회
        Point point = pointService.getPoint(user.id());

        // 3. 쿠폰 조회
        Coupon coupon = command.couponId() != null
                ? couponService.getUserCoupon(user.id(), command.couponId()) : null;

        // 4. 재고를 차감하며 구매상품 목록 상세 조회
        List<Product> products = productService.reduceStockAndGetProducts(command.products());

        // 5. 주문 생성
        Order order = orderService.createOrder(user, products, coupon, point);

        // 6. 결제 요청
        Payment payment = paymentService.processPayment(order, point);

        // 7. 결제 결과에 따른 변경
        if (payment.status() == PaymentStatus.SUCCESS) {
            // 주문 상태 변경
            orderService.updateOrderStatus(order, true);

            // 포인트 차감
            Point deductedPoint = pointService.deductPoint(user.id(), payment.finalPrice());

            // 쿠폰 사용
            if (coupon != null) {
                couponService.useCoupon(user.id(), coupon.id());
            }
        } else {
            // 결제 실패 시 주문 상태 변경
            orderService.updateOrderStatus(order, false);

            // 결제 실패 시 재고 복원
            productService.restoreStock(command.products());
        }

        // 8. 외부 데이터 플랫폼에 주문 정보 전송
        externalDataService.sendOrderData(order);

        // 9. OrderInfo 응답 생성
        return new OrderInfo(
                order.getId(),
                order.getFinalPrice(),
                order.getStatus().toString(),
                payment.status().toString(),
                products
        );
    }

}
