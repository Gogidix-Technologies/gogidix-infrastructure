package com.exalt.warehousing.management.aspect;

import com.exalt.warehousing.management.service.PerformanceTuningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Aspect for monitoring method performance
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "warehouse.performance.monitoring.enabled", havingValue = "true", matchIfMissing = false)
public class PerformanceMonitoringAspect {

    private final PerformanceTuningService performanceTuningService;

    /**
     * Pointcut for service methods
     */
    @Pointcut("execution(* com.ecosystem.warehousing.management.service.*.*(..))")
    public void serviceMethod() {}

    /**
     * Pointcut for repository methods
     */
    @Pointcut("execution(* com.ecosystem.warehousing.management.repository.*.*(..))")
    public void repositoryMethod() {}

    /**
     * Pointcut for controller methods
     */
    @Pointcut("execution(* com.ecosystem.warehousing.management.controller.*.*(..))")
    public void controllerMethod() {}

    /**
     * Advice to monitor service method performance
     *
     * @param joinPoint the join point
     * @return the method result
     * @throws Throwable if the method execution throws an exception
     */
    @Around("serviceMethod() || repositoryMethod() || controllerMethod()")
    public Object monitorMethodPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        // Get method signature
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        // Skip performance monitoring service to avoid recursive monitoring
        if (className.equals("PerformanceTuningService")) {
            return joinPoint.proceed();
        }

        // Record start time
        long startTime = System.currentTimeMillis();

        try {
            // Execute the method
            return joinPoint.proceed();
        } finally {
            // Calculate execution time
            long executionTime = System.currentTimeMillis() - startTime;

            // Record method execution time
            performanceTuningService.recordMethodExecutionTime(className, methodName, executionTime);
        }
    }
} 
