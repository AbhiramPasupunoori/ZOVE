package com.zove.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.zove.app.service.MediaStorageService;

@Configuration
public class MediaConfig implements WebMvcConfigurer {

    private final MediaStorageService mediaStorageService;

    public MediaConfig(MediaStorageService mediaStorageService) {
        this.mediaStorageService = mediaStorageService;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(mediaStorageService.uploadRoot().toUri().toString());
    }
}
