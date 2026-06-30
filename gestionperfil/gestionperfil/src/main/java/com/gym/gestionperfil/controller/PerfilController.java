package com.gym.gestionperfil.controller;

import com.gym.gestionperfil.dto.UsuarioPerfil;
import com.gym.gestionperfil.service.UsuarioClient;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import com.gym.gestionperfil.security.RoleValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@RestController
@RequestMapping("/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final UsuarioClient usuarioClient;
    private final RoleValidator validator;

//

    @Operation(
        summary = "Muestra el perfil del usuario",
        description = "Retorna el perfil con validación (Cliente o Entrenador)"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Perfil obtenido correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @GetMapping
    public ResponseEntity<UsuarioPerfil> verMiPerfil(HttpServletRequest request) {
        Long idUsuario = validator.getUserId(request);
        validator.requireRole(request, "CLIENTE", "ENTRENADOR");

        UsuarioPerfil perfil = usuarioClient.verPerfil(idUsuario);
        return ResponseEntity.ok(perfil);
    }

//

    @Operation(
        summary = "Actualiza perfil",
        description = "Actualiza el perfil de un usuario según su id"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Perfil actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @PutMapping
    public ResponseEntity<UsuarioPerfil> actualizarMiPerfil(@RequestBody UsuarioPerfil dto,
        HttpServletRequest request) {
        Long idUsuario = validator.getUserId(request);
        validator.requireRole(request, "CLIENTE", "ENTRENADOR");

        UsuarioPerfil actualizado = usuarioClient.actualizarPerfil(idUsuario, dto);
        return ResponseEntity.ok(actualizado);
    }

//

    @Operation(
        summary = "Cambia contraseña",
        description = "Cambia la contraseña según la el id"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Contraseña modificada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")  ,
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
})

//

    @PutMapping("/cambiar-clave")
    public ResponseEntity<String> cambiarPassword(@RequestBody Map<String, String> requestBody,
        HttpServletRequest request) {
        Long idUsuario = validator.getUserId(request);
        validator.requireRole(request, "CLIENTE", "ENTRENADOR");

        try {
            usuarioClient.cambiarClave(
                    idUsuario,
                    requestBody.get("nuevaClave"),
                    requestBody.get("confirmarClave")
            );
            return ResponseEntity.ok("Contraseña cambiada correctamente.");
        } catch (WebClientResponseException.BadRequest e) {
            return ResponseEntity.badRequest().body("Error: " + e.getResponseBodyAsString());
        } catch (WebClientResponseException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }
    }

}

