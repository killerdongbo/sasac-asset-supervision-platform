package com.sasac.platform.supervision.audit.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sasac.platform.supervision.audit.annotation.Auditable;
import com.sasac.platform.supervision.audit.entity.OperationLog;
import com.sasac.platform.supervision.audit.mapper.OperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * AOP aspect that intercepts methods annotated with {@link Auditable}
 * and writes an {@link OperationLog} entry capturing before/after state.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogMapper operationLogMapper;
    private final ObjectMapper objectMapper;

    /**
     * Around advice for auditable methods. Captures method arguments before
     * execution and the return value after execution, then persists a log entry.
     *
     * @param joinPoint the join point
     * @param auditable the Auditable annotation
     * @return the result of the intercepted method
     * @throws Throwable if the intercepted method throws
     */
    @Around("@annotation(auditable)")
    public Object aroundAuditable(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        String beforeData = toJson(joinPoint.getArgs());

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            log.error("Audited method threw exception: {}", e.getMessage());
            throw e;
        }

        String afterData = toJson(result);
        String ip = resolveClientIp();

        OperationLog logEntry = new OperationLog();
        logEntry.setOperatorId(0L); // Will be set from security context in production
        logEntry.setOperatorName("system");
        logEntry.setAction(auditable.action());
        logEntry.setTargetType(auditable.targetType());
        logEntry.setTargetId(extractTargetId(result, joinPoint.getArgs()));
        logEntry.setBeforeData(beforeData);
        logEntry.setAfterData(afterData);
        logEntry.setIp(ip);
        logEntry.setTenantId(0L);
        logEntry.setCreatedAt(LocalDateTime.now());

        try {
            operationLogMapper.insert(logEntry);
        } catch (Exception e) {
            log.warn("Failed to persist operation log: {}", e.getMessage());
        }

        return result;
    }

    /**
     * Extracts a target ID from the return value or method arguments.
     * Looks for an 'id' field via JSON or uses the first Long argument.
     */
    private Long extractTargetId(Object result, Object[] args) {
        if (result != null) {
            try {
                String json = objectMapper.writeValueAsString(result);
                if (json.contains("\"id\"")) {
                    var node = objectMapper.readTree(json);
                    var idNode = node.get("id");
                    if (idNode != null && !idNode.isNull()) {
                        return idNode.asLong();
                    }
                }
            } catch (Exception ignored) {
                // fall through
            }
        }
        // Fallback: find the first Long argument
        return Arrays.stream(args)
                .filter(a -> a instanceof Long)
                .map(a -> (Long) a)
                .findFirst()
                .orElse(null);
    }

    /**
     * Serializes an object to JSON string, returning "[]" or "{}" on failure.
     */
    private String toJson(Object obj) {
        if (obj == null) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }

    /**
     * Resolves the client IP address from the current request context.
     */
    private String resolveClientIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isBlank()) {
                    ip = request.getRemoteAddr();
                }
                return ip;
            }
        } catch (Exception ignored) {
            // no request context available
        }
        return "unknown";
    }
}
