package com.gym.gestionperfil.controller;

import com.gym.gestionperfil.dto.UsuarioPerfil;
import com.gym.gestionperfil.security.RoleValidator;
import com.gym.gestionperfil.service.UsuarioClient;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de PerfilController usando Mockito.
 * UsuarioClient se simula porque internamente hace una llamada HTTP
 * al microservicio de usuarios (no se debe levantar esa dependencia real).
 */
@ExtendWith(MockitoExtension.class)
class PerfilControllerTest {

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private RoleValidator validator;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private PerfilController perfilController;

    private UsuarioPerfil perfil;

    @BeforeEach
    void setUp() {
        perfil = new UsuarioPerfil(
                "12345678-9", "Juan", "Andres", "Perez", "Soto", "juan.perez@gmail.com"
        );
    }

    // ───────────────────────── verMiPerfil ─────────────────────────

    @Test
    void verMiPerfil_devuelvePerfilDelUsuarioAutenticado() {
        when(validator.getUserId(request)).thenReturn(2L);
        when(usuarioClient.verPerfil(2L)).thenReturn(perfil);

        ResponseEntity<UsuarioPerfil> response = perfilController.verMiPerfil(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("juan.perez@gmail.com", response.getBody().getCorreo());
        verify(validator).requireRole(request, "CLIENTE", "ENTRENADOR");
    }

    // ───────────────────────── actualizarMiPerfil ─────────────────────────

    @Test
    void actualizarMiPerfil_datosValidos_retorna200ConPerfilActualizado() {
        UsuarioPerfil nuevosDatos = new UsuarioPerfil(
                "12345678-9", "Carla", "Maria", "Rojas", "Vidal", "carla@gmail.com"
        );
        when(validator.getUserId(request)).thenReturn(2L);
        when(usuarioClient.actualizarPerfil(2L, nuevosDatos)).thenReturn(nuevosDatos);

        ResponseEntity<UsuarioPerfil> response = perfilController.actualizarMiPerfil(nuevosDatos, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("carla@gmail.com", response.getBody().getCorreo());
    }

    // ───────────────────────── cambiarPassword ─────────────────────────

    @Test
    void cambiarPassword_datosValidos_retorna200() {
        Map<String, String> body = Map.of(
                "nuevaClave", "NuevaClave123",
                "confirmarClave", "NuevaClave123"
        );
        when(validator.getUserId(request)).thenReturn(2L);
        doNothing().when(usuarioClient).cambiarClave(2L, "NuevaClave123", "NuevaClave123");

        ResponseEntity<String> response = perfilController.cambiarPassword(body, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Contraseña cambiada correctamente.", response.getBody());
    }

    @Test
    void cambiarPassword_errorBadRequestDelServicioUsuarios_retorna400() {
        Map<String, String> body = Map.of(
                "nuevaClave", "abc",
                "confirmarClave", "abc"
        );
        when(validator.getUserId(request)).thenReturn(2L);

        WebClientResponseException.BadRequest badRequest =
                (WebClientResponseException.BadRequest) WebClientResponseException.create(
                        400, "Bad Request", new HttpHeaders(),
                        "La clave debe tener entre 5 y 15 caracteres".getBytes(StandardCharsets.UTF_8),
                        StandardCharsets.UTF_8
                );
        doThrow(badRequest).when(usuarioClient).cambiarClave(2L, "abc", "abc");

        ResponseEntity<String> response = perfilController.cambiarPassword(body, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void cambiarPassword_usuarioNoEncontrado_retorna404() {
        Map<String, String> body = Map.of(
                "nuevaClave", "NuevaClave123",
                "confirmarClave", "NuevaClave123"
        );
        when(validator.getUserId(request)).thenReturn(99L);

        WebClientResponseException.NotFound notFound =
                (WebClientResponseException.NotFound) WebClientResponseException.create(
                        404, "Not Found", new HttpHeaders(),
                        new byte[0], StandardCharsets.UTF_8
                );
        doThrow(notFound).when(usuarioClient).cambiarClave(99L, "NuevaClave123", "NuevaClave123");

        ResponseEntity<String> response = perfilController.cambiarPassword(body, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Usuario no encontrado.", response.getBody());
    }
}
