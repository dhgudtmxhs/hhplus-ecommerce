package kr.hhplus.be.server.order.infra;

import kr.hhplus.be.server.order.domain.Order;
import kr.hhplus.be.server.order.domain.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository orderJpaRepository; // JPA Repository
    private final OrderMapper orderMapper;               // MapStruct Mapper

    @Override
    public Order save(Order order) {
        return orderMapper.toDomain(orderJpaRepository.save(orderMapper.toEntity(order)));
    }
}
