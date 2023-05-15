package com.cleverpine.springlogginglibrary.aop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class PerformanceMeasureAspect {

    private static final Logger logger = LogManager.getLogger(PerformanceMeasureAspect.class);

    @Around("@annotation(com.cleverpine.springlogginglibrary.aop.PerformanceMeasure)")
    public Object measurePerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("Method {} executed in {} ms", joinPoint.getSignature().toShortString(), executionTime);

        return result;
    }
}

