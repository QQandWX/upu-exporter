package com.prometheus.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.prometheus.utils.ShareMemory;

@Configuration
public class ShareMemoryConfiguration {
	
	
    @Bean(initMethod = "init")
    public ShareMemory ShareMemory() {
    	ShareMemory shareMemory = new ShareMemory();
        return shareMemory;
    }
}
