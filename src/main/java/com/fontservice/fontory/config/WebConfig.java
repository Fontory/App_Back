package com.fontservice.fontory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.*;


@Configuration
public class WebConfig implements WebMvcConfigurer {

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

        registry.addResourceHandler("/profiles/**")
                .addResourceLocations("file:./uploads/profiles/");

        registry.addResourceHandler("/handwriting/**")
                .addResourceLocations("file:./uploads/handwriting/");
    }

    // CORS 설정 추가
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 API 경로에 대해
                .allowedOrigins(
                        "http://localhost:3000",
                        "http://ceprj.gachon.ac.kr:3000"
                ) // 프론트 주소
                .allowedMethods("*") // GET, POST 등 모두 허용
                .allowCredentials(true);
    }

}
