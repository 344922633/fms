package com.fms.config;

import com.fms.utils.CustomWebBindingInitializer;
import com.fms.utils.JarLoadUtil;
import com.google.common.collect.Lists;
import com.handu.apollo.utils.json.JsonUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.List;
@EnableWebMvc
public class MvcConfig extends WebMvcConfigurerAdapter{
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(mappingJackson2HttpMessageConverter());
    }

    @Bean
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {

        List<HttpMessageConverter<?>> httpMessageConverterList = Lists.newArrayList();
        httpMessageConverterList.add(mappingJackson2HttpMessageConverter());

        RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter();
        adapter.setMessageConverters(httpMessageConverterList);

        adapter.setWebBindingInitializer(new CustomWebBindingInitializer());

        return adapter;
    }

    @Bean
    public HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter httpMessageConverter = new MappingJackson2HttpMessageConverter();
        httpMessageConverter.setObjectMapper(JsonUtil.getMapper());
        return httpMessageConverter;
    }
}
