package com.example.template.config;

import com.example.template.utils.interceptor.CommonInterceptor;
import com.example.template.utils.interceptor.UserAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final UserAuthInterceptor userAuthInterceptor;
    private final CommonInterceptor commonInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(commonInterceptor)
                .addPathPatterns("/api/v1/**");

        registry.addInterceptor(userAuthInterceptor)
                .addPathPatterns("/api/v1/auth/**")
                .excludePathPatterns("/api/v1/auth");
    }
}
