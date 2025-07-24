package com.bottle.muselink.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")       // 匹配路径
                .allowedOrigins("http://localhost:3000")  // 允许的来源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的方法
                .allowedHeaders("*")        // 允许的头信息
                .exposedHeaders("*") // 暴露的头信息
                .allowCredentials(true)     // 是否允许携带 Cookie
                .maxAge(3600);              // 预检请求缓存时间（秒）
    }
}
