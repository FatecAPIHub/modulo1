package br.com.fatec.modulo1.pessoa_api.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        String requestId = UUID.randomUUID().toString();

        MDC.put("requestId", requestId);
        MDC.put("method", request.getMethod());
        MDC.put("path", request.getRequestURI());
        MDC.put("clientIp", getClientIpAddress(request));

        response.setHeader("X-Request-Id", requestId);

        try {
            logger.info("Request started");

            filterChain.doFilter(request, response);

            long duration = System.currentTimeMillis() - startTime;
            MDC.put("duration", String.valueOf(duration));
            MDC.put("statusCode", String.valueOf(response.getStatus()));

            logger.info("Request completed");

        } finally {
            MDC.clear();
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
