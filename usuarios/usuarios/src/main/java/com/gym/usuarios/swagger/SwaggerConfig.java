package com.gym.usuarios.swagger;

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
        name        = "X-User-Roles",
        type        = SecuritySchemeType.APIKEY,
        in          = SecuritySchemeIn.HEADER,
        paramName   = "X-User-Roles",
        description = "Rol del usuario. Valores válidos: ADMINISTRADOR, SOPORTE, CLIENTE, ENTRENADOR"
    ),
    @SecurityScheme(
        name        = "X-User-Id",
        type        = SecuritySchemeType.APIKEY,
        in          = SecuritySchemeIn.HEADER,
        paramName   = "X-User-Id",
        description = "ID numérico del usuario autenticado (requerido solo en /usuarios/perfil)"
    )
})
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API Microservicio Usuarios - Gym")
                .version("1.0")
                .description(
                    "Gestión de usuarios, roles y estados de usuario.\n\n" +
                    "**Instrucciones para usar Swagger UI:**\n" +
                    "1. Haz clic en el botón **Authorize** 🔒 (arriba a la derecha)\n" +
                    "2. En **X-User-Roles** escribe: `ADMINISTRADOR`\n" +
                    "3. En **X-User-Id** escribe el id del usuario, ej: `1` " +
                       "(solo requerido en `GET /usuarios/perfil` y `PUT /usuarios/perfil`)\n" +
                    "4. Haz clic en **Authorize** → **Close**\n\n" +
                    "Los headers se enviarán automáticamente en cada request."
                )
            )
            .addSecurityItem(new SecurityRequirement()
                .addList("X-User-Roles")
                .addList("X-User-Id")
            );
    }
}
