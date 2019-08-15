package com.zhao.upms.web;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasee on 2019/2/12.
 */
@SpringBootApplication
public class UpmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(UpmsApplication.class, args);
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customJackson() {
        return new Jackson2ObjectMapperBuilderCustomizer() {

            @Override
            public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
//                jacksonObjectMapperBuilder.serializationInclusion(JsonInclude.Include.NON_NULL);
                jacksonObjectMapperBuilder.failOnUnknownProperties(false);
                jacksonObjectMapperBuilder.propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            }

        };
    }

    @Bean
    RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
