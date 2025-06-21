package com.recime.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI recipeOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Recipe Management API")
                        .description("RESTful API for managing cooking recipes")
                        .version("1.0.0"));
    }
}