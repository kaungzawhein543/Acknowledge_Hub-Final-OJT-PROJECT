package com.ace.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:///D:/OJT-14/Intellij/Acknowledge_Hub-Final-OJT-PROJECT/acKnowledgeHub(backend)/src/main/resources/images/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(3600)).cachePublic());

        //for default profile folder
        registry.addResourceHandler("/defaultProfile/**")
                .addResourceLocations("file:///D:/OJT-14/Intellij/Acknowledge_Hub-Final-OJT-PROJECT/acKnowledgeHub(backend)/src/main/resources/defaultProfile/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(3600)).cachePublic());
    }


}