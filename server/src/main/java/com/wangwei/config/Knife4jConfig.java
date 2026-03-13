package com.wangwei.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("校园签到系统接口文档")
                        .version("1.0")
                        .description("Campus Checkin API")
                        .contact(new Contact()
                                .name("L学长")
                                .email("3530812934@qq.com")));
    }

    // admin模块
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin接口")
                .packagesToScan("com.wangwei.controller.admin")
                .build();
    }

    // user模块
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("user接口")
                .packagesToScan("com.wangwei.controller.user")
                .build();
    }
}