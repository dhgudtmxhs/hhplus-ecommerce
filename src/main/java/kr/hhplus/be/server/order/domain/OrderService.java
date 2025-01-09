package kr.hhplus.be.server.order.domain;

import kr.hhplus.be.server.coupon.domain.Coupon;
import kr.hhplus.be.server.order.application.OrderCommand;
import kr.hhplus.be.server.point.domain.Point;
import kr.hhplus.be.server.product.domain.Product;
import kr.hhplus.be.server.product.domain.ProductRepository;
import kr.hhplus.be.server.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public Order createOrder(User user, List<Product> products, Coupon coupon, Point point) {

        Order order = Order.create(user.id(), products, coupon, point.point());

        return orderRepository.save(order);
    }

    public void updateOrderStatus(Order order, boolean paymentSuccess) {
        if (paymentSuccess) {
            order.markAsPaid();
        } else {
            order.markAsFailed();
        }
        // 변경된 상태를 저장
        orderRepository.save(order);
    }
}
