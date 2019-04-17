package com.prometheus.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.prometheus.acpect.MyTimeAcpect;


import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class TimedConfiguration {
	   @Bean
	   public MyTimeAcpect timedAspect(MeterRegistry registry) {
	      return new MyTimeAcpect(registry);
	   }
}
