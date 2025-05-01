package com.fontservice.fontory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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

        registry.addResourceHandler("/handwriting/**")
                .addResourceLocations("file:./uploads/handwriting/");
    }

}
