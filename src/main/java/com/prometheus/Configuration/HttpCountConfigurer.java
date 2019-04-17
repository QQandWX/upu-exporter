package com.prometheus.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.prometheus.adapter.HttpCountAdapter;

@Component
public class HttpCountConfigurer  implements WebMvcConfigurer{
    @Autowired
    private HttpCountAdapter httpCountAdapter;

 /*   @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor(httpCountAdapter);
    }*/

}
