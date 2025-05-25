package com.fontservice.fontory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // ✅ 정적 리소스 핸들러
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/backgrounds/**")
                .addResourceLocations("file:./uploads/backgrounds/");

        registry.addResourceHandler("/fonts/**")
                .addResourceLocations("file:./uploads/fonts/");

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:./uploads/images/");

        registry.addResourceHandler("/preview/**")
                .addResourceLocations("file:./uploads/preview/");

        registry.addResourceHandler("/handwriting/**")
                .addResourceLocations("file:./uploads/handwriting/");

        registry.addResourceHandler("/profiles/**")
                .addResourceLocations("file:./uploads/profiles/");

        registry.addResourceHandler("/post/**")
                .addResourceLocations("file:./uploads/post/");                
    }

    // ✅ CORS 허용 설정
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://10.0.2.2:8081") // 👈 리액트 네이티브 에뮬레이터에서의 localhost
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true); // 세션 유지 (쿠키 포함)
    }
}
