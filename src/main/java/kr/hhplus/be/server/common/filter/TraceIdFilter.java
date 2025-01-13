package kr.hhplus.be.server.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class TraceIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String traceId = generateTraceId();
        request.setAttribute("traceId", traceId);
        response.setHeader("X-Trace-Id", traceId);

        MDC.put("traceId", traceId); // MDC에 Trace ID 설정

        try {
            filterChain.doFilter(request, response); // 다음 필터 호출
        } finally {
            MDC.remove("traceId"); // 요청 처리 후 MDC 값 제거
        }
    }

    private String generateTraceId() {
        return java.util.UUID.randomUUID().toString();
    }
}