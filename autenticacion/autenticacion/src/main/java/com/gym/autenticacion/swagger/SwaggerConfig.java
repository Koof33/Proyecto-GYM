package com.gym.autenticacion.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API Autenticacion - Gym")
                .version("1.0")
                .description(
                    "Servicio de autenticación. Genera tokens JWT.\n\n" +
                    "**Endpoints públicos, no requieren headers.**\n\n" +
                    "Usuarios precargados:\n" +
                    "- `admin@gimnasio.com` / `admin123`\n" +
                    "- `cliente@gimnasio.com` / `cliente123`\n" +
                    "- `entrenador@gimnasio.com` / `entrenador123`\n" +
                    "- `soporte@gimnasio.com` / `soporte123`"
                )
            );
    }
}
