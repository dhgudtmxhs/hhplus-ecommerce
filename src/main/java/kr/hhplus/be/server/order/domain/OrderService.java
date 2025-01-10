package kr.hhplus.be.server.order.domain;

import kr.hhplus.be.server.coupon.domain.Coupon;
import kr.hhplus.be.server.order.application.OrderCommand;
import kr.hhplus.be.server.order.application.ProductOrderCommand;
import kr.hhplus.be.server.point.domain.Point;
import kr.hhplus.be.server.product.domain.Product;
import kr.hhplus.be.server.product.domain.ProductRepository;
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
    public Order createOrder(User user, List<Product> products, Coupon coupon, Point point, List<ProductOrderCommand> productOrderCommands) {
        // Product ID와 주문 수량 매핑
        Map<Long, Long> productQuantityMap = productOrderCommands.stream()
                .collect(Collectors.toMap(ProductOrderCommand::productId, ProductOrderCommand::quantity));

        // Product -> OrderItem 변환 (productId 기준으로 수량 매핑)
        List<OrderItem> orderItems = products.stream()
                .map(product -> new OrderItem(
                        null,
                        product.id(),
                        productQuantityMap.get(product.id()), // 수량을 올바르게 매칭
                        product.price()                       // 가격 매칭
                ))
                .collect(Collectors.toList());

        // Order 생성
        Order order = Order.create(user.id(), orderItems, coupon, point.point());

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
