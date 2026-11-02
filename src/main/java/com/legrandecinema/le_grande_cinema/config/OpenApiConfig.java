package com.legrandecinema.le_grande_cinema.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cinemaOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("Le Grande Cinema API")
                                .version("1.0")
                                .description("API REST de l'application Le Grande Cinema")
                )
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "basicAuth",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("basic")
                                )
                );
    }
}