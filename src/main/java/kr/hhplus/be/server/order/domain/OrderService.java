package kr.hhplus.be.server.order.domain;

import kr.hhplus.be.server.coupon.domain.Coupon;
import kr.hhplus.be.server.order.application.ProductOrderCommand;
import kr.hhplus.be.server.point.domain.Point;
import kr.hhplus.be.server.product.domain.Product;
import kr.hhplus.be.server.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public Order createOrder(Long UserId, List<Product> products, Coupon coupon, Point point) {
        // Product ID와 주문 수량 매핑 (Map<Long, Integer>로 수정)

        // 할인 금액 추출
        Long discountAmount = coupon != null ? coupon.getDiscountAmount() : 0L;
        Long userCouponId = coupon != null ? coupon.getId() : null;

        // Product -> OrderItem 변환 (productId 기준으로 수량 매핑)
        List<OrderItem> orderItems = products.stream()
                .map(product -> OrderItem.builder()
                        .productId(product.getId())
                        .price(product.getPrice())
                        .quantity(1L) // 임시
                        .build())
                .collect(Collectors.toList());

        // Order 생성
        Order order = Order.create(UserId, orderItems, userCouponId, discountAmount, point.getPoint());

        return orderRepository.save(order);
    }

    public void updateOrderStatus(Order order, boolean paymentSuccess) {
        if (paymentSuccess) {
            order.markAsPaid();
        } else {
            order.markAsFailed();
        }
        orderRepository.save(order);
    }
}