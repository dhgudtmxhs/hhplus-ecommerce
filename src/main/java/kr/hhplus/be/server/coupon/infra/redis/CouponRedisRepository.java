package kr.hhplus.be.server.coupon.infra.redis;

import kr.hhplus.be.server.coupon.domain.CouponIssueResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponRedisRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    // 쿠폰 발급 - 원자적으로 쿠폰 발급 및 재고 감소 처리
    public CouponIssueResult requestCoupon(Long userId, Long couponId) {
        String luaScript =
                "local success, result = pcall(function() " +
                        "   local user_id = KEYS[1] " +
                        "   local coupon_key = KEYS[2] " +
                        "   local coupon_id = ARGV[1] " +
                        "   local issued_set = coupon_key .. \":issued\" " +
                        "   local stock_key = \"COUPON_STOCK:\" .. coupon_id " +
                        "   if redis.call(\"SISMEMBER\", issued_set, user_id) == 1 then " +
                        "       return \"Coupon already issued\" " +
                        "   end " +
                        "   local stock = tonumber(redis.call(\"GET\", stock_key)) " +
                        "   if not stock or stock <= 0 then " +
                        "       return \"Coupon issued closed\" " +
                        "   end " +
                        "   redis.call(\"DECR\", stock_key) " +
                        "   redis.call(\"SADD\", issued_set, user_id) " +
                        "   return \"Coupon issued\" " +
                        "end) " +
                        "if not success then " +
                        "   return \"Internal error\" " +
                        "else " +
                        "   return result " +
                        "end";

        // 임시로 값 직렬화기를 StringRedisSerializer로 변경
        var originalSerializer = redisTemplate.getValueSerializer();
        redisTemplate.setValueSerializer(new org.springframework.data.redis.serializer.StringRedisSerializer());

        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>(luaScript, String.class);
        String couponKey = "coupon:" + couponId;
        String result = redisTemplate.execute(redisScript,
                List.of(userId.toString(), couponKey),
                couponId.toString());

        // 원래 직렬화기로 복원
        redisTemplate.setValueSerializer(originalSerializer);

        return CouponIssueResult.fromValue(result);
    }

    // DB insert 실패한 경우 Redis 롤백 - 쿠폰 발급받은 유저 목록에서 제거, 재고 다시 증가
    public void cancelCoupon(Long userId, Long couponId) {
        String luaScript =
                "local success, result = pcall(function() " +
                        "   local user_id = KEYS[1] " +
                        "   local coupon_key = KEYS[2] " +
                        "   local coupon_id = ARGV[1] " +
                        "   local issued_set = coupon_key .. \":issued\" " +
                        "   local stock_key = \"COUPON_STOCK:\" .. coupon_id " +
                        "   if redis.call(\"SISMEMBER\", issued_set, user_id) == 1 then " +
                        "       redis.call(\"SREM\", issued_set, user_id) " +
                        "       redis.call(\"INCR\", stock_key) " +
                        "       return \"Coupon rollback success\" " +
                        "   else " +
                        "       return \"No issued coupon found\" " +
                        "   end " +
                        "end) " +
                        "if not success then " +
                        "   return \"Internal error\" " +
                        "else " +
                        "   return result " +
                        "end";

        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>(luaScript, String.class);
        String couponKey = "coupon:" + couponId;
        String result = redisTemplate.execute(redisScript,
                List.of(userId.toString(), couponKey),
                couponId.toString());

        log.info("쿠폰 레디스 롤백 : userId={}, couponId={}, result={}", userId, couponId, result);

    }
}