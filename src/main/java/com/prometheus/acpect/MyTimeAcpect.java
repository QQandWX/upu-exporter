package com.prometheus.acpect;

import java.lang.reflect.Method;
import java.util.function.Function;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.lang.NonNullApi;
@Aspect
@NonNullApi
public class MyTimeAcpect {
    public static final String DEFAULT_METRIC_NAME = "method.timed";
    public static final String EXCEPTION_TAG = "exception";

    private final MeterRegistry registry;
    public MyTimeAcpect(MeterRegistry registry) {
        this.registry = registry;
    }
    
   // @Pointcut("@annotation(com.prometheus.acpect.MyTimed)")
    @Pointcut("execution(public * com.prometheus..*.*(..))")
    public void pcMethod() {
    }
   //@Pointcut("execution(public * com.prometheus.controller..*.*(..))")
    //@Around("execution (@com.prometheus.acpect.MyTimed * *.*(..))")
    @Around("execution (@com.prometheus.acpect.MyTimed * com.prometheus..*.*(..))")
    public Object timedMethod(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        System.out.println("===============================================aop切面："+method+"===============================================");
        MyTimed timed = method.getAnnotation(MyTimed.class);
        if (timed == null) {
            method = pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
            timed = method.getAnnotation(MyTimed.class);
        }

        final String metricName = timed.value().isEmpty() ? DEFAULT_METRIC_NAME : timed.value();
        Timer.Sample sample = Timer.start(registry);
        String exceptionClass = "none";

        try {
            return pjp.proceed();
        } catch (Exception ex) {
            exceptionClass = ex.getClass().getSimpleName();
            throw ex;
        } finally {
            try {
                sample.stop(Timer.builder(metricName)
                        .register(registry));
            } catch (Exception e) {
                // ignoring on purpose
            }
        }
    }
}
