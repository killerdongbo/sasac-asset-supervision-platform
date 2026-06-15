package com.sasac.platform.permission.aspect;

import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.permission.annotation.RequirePermission;
import com.sasac.platform.permission.context.PermContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionAspect {

    @Around("@annotation(com.sasac.platform.permission.annotation.RequirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature sig = (MethodSignature) joinPoint.getSignature();
        RequirePermission annotation = sig.getMethod().getAnnotation(RequirePermission.class);

        // 检查角色
        String[] requiredRoles = annotation.roles();
        boolean hasRole = false;
        if (requiredRoles.length == 0) {
            hasRole = true; // 不限制角色则放行
        } else {
            for (String role : requiredRoles) {
                if (PermContext.hasRole(role)) {
                    hasRole = true;
                    break;
                }
            }
        }

        // 检查权限
        String permCode = annotation.value();
        if (!permCode.isBlank() && !PermContext.hasPermission(permCode)) {
            throw new BusinessException("无操作权限: " + permCode);
        }

        if (!hasRole) {
            throw new BusinessException("无角色权限，需要: " + String.join(",", requiredRoles));
        }

        return joinPoint.proceed();
    }
}
