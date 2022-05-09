package com.scb.rider.joballocation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@ComponentScan("com.scb.rider.joballocation.controller")
public class SwaggerConfig {


  @Bean
  public Docket appApi() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        .useDefaultResponseMessages(false)
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.any())
        .build();
  }

  public ApiInfo apiInfo() {
    final ApiInfoBuilder builder = new ApiInfoBuilder();
    builder.title("Swagger Test App").version("1.0").license("(C) Copyright Test")
        .description("The API provides a platform to query build test swagger api");

    return builder.build();
  }

}
