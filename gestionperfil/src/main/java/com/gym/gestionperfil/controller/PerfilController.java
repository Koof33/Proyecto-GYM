package com.gym.gestionperfil.controller;

import com.gym.gestionperfil.dto.UsuarioPerfil;
import com.gym.gestionperfil.service.UsuarioClient;
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

    @GetMapping
    public ResponseEntity<UsuarioPerfil> verMiPerfil(HttpServletRequest request) {
        Long idUsuario = validator.getUserId(request);
        validator.requireRole(request, "CLIENTE", "ENTRENADOR");

        UsuarioPerfil perfil = usuarioClient.verPerfil(idUsuario);
        return ResponseEntity.ok(perfil);
    }

    @PutMapping
    public ResponseEntity<UsuarioPerfil> actualizarMiPerfil(@RequestBody UsuarioPerfil dto,
                                                               HttpServletRequest request) {
        Long idUsuario = validator.getUserId(request);
        validator.requireRole(request, "CLIENTE", "ENTRENADOR");

        UsuarioPerfil actualizado = usuarioClient.actualizarPerfil(idUsuario, dto);
        return ResponseEntity.ok(actualizado);
    }

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

