package com.malgn.configure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.dir}")
    private String fileDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // localhost:8080/images/abc.png 로 요청이 오면
        // 설정된 fileDir 폴더 안에서 파일을 찾아라!
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + fileDir);
    }
}