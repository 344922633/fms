package com.fms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@ComponentScan(basePackages = {"com.handu.apollo", "com.fms"})
@Import({MvcConfig.class, FastdfsConfig.class})
@PropertySource("classpath:/application.properties")
public class AppConfig {
    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(10485760000L);//<!-- 最大上传文件大小 -->
//        multipartResolver.setMaxUploadSize(10485760L);//<!-- 最大上传文件大小 -->
        multipartResolver.setMaxInMemorySize(10960);
        multipartResolver.setResolveLazily(true);
        multipartResolver.setDefaultEncoding("utf-8");
        return multipartResolver;
    }
    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        RequestMappingHandlerMapping mapping = new RequestMappingHandlerMapping();
        Object[] objects = new Object[] {
        };
        mapping.setInterceptors(objects);
        return mapping;
    }
    /**
     * jackson对象声明
     */
    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return objectMapper;
    }
}
