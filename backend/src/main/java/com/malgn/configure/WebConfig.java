package com.malgn.configure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.dir}")
    private String fileDir;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해
                .allowedOrigins("http://localhost:5173") // 프론트엔드 주소 허용
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true) // 쿠키 포함 허용 (필수)
                .maxAge(3600); // Preflight 캐시 시간
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // localhost:8080/images/abc.png 로 요청이 오면
        // 설정된 fileDir 폴더 안에서 파일을 찾아라!
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + fileDir);
    }
}