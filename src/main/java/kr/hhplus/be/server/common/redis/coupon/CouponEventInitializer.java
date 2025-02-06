package kr.hhplus.be.server.common.redis.coupon;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CouponEventInitializer {
    private final RedisTemplate<String, Object> redisTemplate;

    public void initializeCouponStock(Long couponId, int initialStock, Duration ttl) {
        redisTemplate.opsForValue().set("COUPON_STOCK:" + couponId, initialStock, ttl);

        redisTemplate.expire("coupon:" + couponId + ":issued", ttl);
        redisTemplate.expire("coupon:" + couponId + ":requests", ttl);
    }
}