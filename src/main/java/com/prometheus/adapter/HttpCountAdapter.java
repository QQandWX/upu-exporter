package com.prometheus.adapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry.Config;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;


@Component
public class HttpCountAdapter extends HandlerInterceptorAdapter{
	private static final Counter COUNTER = Counter.builder("Http请求统计")
            .tag("HttpCount-wjf", "HttpCount-wjf")
            .description("Http请求统计")
            .register(Metrics.globalRegistry);

	   @Override
	    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
	                                Object handler, Exception ex) throws Exception {
	        COUNTER.increment();
	        

	        
	        
	        
	    }
}
