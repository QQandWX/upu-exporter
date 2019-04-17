package com.prometheus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;

@SpringBootApplication
@EnableScheduling
public class ActuatorApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(ActuatorApplication.class, args);
		//ZkClient zkClient = context.getBean(ZkClient.class);
        //zkClient.register();
		
		
		
	}
	
	  @Bean
	  MeterRegistryCustomizer<MeterRegistry> meterRegistryCustomizer() {
	    //return registry -> registry.config().commonTags("commontag1", "a", "commontag2", "b");
	    return registry -> registry.config().meterFilter(MeterFilter.denyNameStartsWith("jvm"))
	    									.meterFilter(MeterFilter.denyNameStartsWith("http"))
	    		                            .meterFilter(MeterFilter.denyNameStartsWith("tomcat"))
	    		                            .meterFilter(MeterFilter.denyNameStartsWith("logback"))
	    		                            .meterFilter(MeterFilter.denyNameStartsWith("system"))
	    		                            .meterFilter(MeterFilter.denyNameStartsWith("process"))
	    		                            .commonTags("commontag1", "a", "commontag2", "b");
	  }
	
	  
	    @Override//为了打包springboot项目
	    protected SpringApplicationBuilder configure(
	            SpringApplicationBuilder builder) {
	        return builder.sources(this.getClass());
	    }
	  
}
