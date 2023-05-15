package com.cleverpine.springlogginglibrary.configuration;

import com.cleverpine.springlogginglibrary.interceptors.ServiceAndTraceIdLoggingInterceptor;
import com.cleverpine.springlogginglibrary.models.LoggingInfoContext;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


import static com.cleverpine.springlogginglibrary.util.Constants.SERVICE_ID;

@Configuration
public class CPSpringLoggingConfig implements WebMvcConfigurer {

    @Value("${spring.application.name}")
    private String serviceId;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ServiceAndTraceIdLoggingInterceptor());
    }

    @Bean
    public void setServiceId(){
        var ctx = LoggingInfoContext.getInstance();
        ctx.setServiceId(serviceId);
        ThreadContext.put(SERVICE_ID, serviceId);
    }
}
