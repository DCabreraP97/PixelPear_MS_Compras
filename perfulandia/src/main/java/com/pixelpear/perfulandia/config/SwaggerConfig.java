package com.pixelpear.perfulandia.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Perfulandia-API")
                .version("2.0.1")
                .description("Documentación de la API Restful de Perfulandia, microservicio de compras de perfumes.")
                .contact(new Contact()
                    .name("Equipo PixelPear")
                    .email("soporte@pixelpear.cl")
                    .url("https://pixelpear.cl")
                )
            );
    }
}
