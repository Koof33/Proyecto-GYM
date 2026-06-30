package com.gym.membresias.swagger;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecuritySchemes({
    @SecurityScheme(
        name      = "X-User-Roles",
        type      = SecuritySchemeType.APIKEY,
        in        = SecuritySchemeIn.HEADER,
        paramName = "X-User-Roles",
        description = "Rol del usuario. Valores: ADMINISTRADOR, CLIENTE, ENTRENADOR, SOPORTE"
    ),
    @SecurityScheme(
        name      = "X-User-Id",
        type      = SecuritySchemeType.APIKEY,
        in        = SecuritySchemeIn.HEADER,
        paramName = "X-User-Id",
        description = "ID numérico del usuario autenticado"
    )
})
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API Membresias y Planes - Gym")
                .version("1.0")
                .description(
                    "**Instrucciones:**\n" +
                    "1. Haz clic en **Authorize** 🔒\n" +
                    "2. En **X-User-Roles** escribe el rol requerido (ej: `ADMINISTRADOR`)\n" +
                    "3. En **X-User-Id** escribe el id del usuario (ej: `1`)\n" +
                    "4. Clic en **Authorize** → **Close**"
                )
            )
            .addSecurityItem(new SecurityRequirement()
                .addList("X-User-Roles")
                .addList("X-User-Id")
            );
    }
}
