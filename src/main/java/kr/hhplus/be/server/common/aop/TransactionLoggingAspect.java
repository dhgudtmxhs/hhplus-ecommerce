package kr.hhplus.be.server.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Aspect
@Component
public class TransactionLoggingAspect {

    @Before("@annotation(transactional)")
    public void logTransactionStart(JoinPoint joinPoint, Transactional transactional) {
        log.info("트랜잭션 시작: {}.{}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
    }

    @After("@annotation(transactional)")
    public void logTransactionCommit(JoinPoint joinPoint, Transactional transactional) {
        log.info("트랜잭션 커밋: {}.{}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
    }

    @AfterThrowing(pointcut = "@annotation(transactional)", throwing = "ex")
    public void logTransactionRollback(JoinPoint joinPoint, Transactional transactional, Throwable ex) {
        log.error("트랜잭션 롤백: {}.{} due to {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                ex.getMessage());
    }
}
