package com.gym.autenticacion.controller;

import com.gym.autenticacion.dto.LoginRequest;
import com.gym.autenticacion.dto.LoginResponse;
import com.gym.autenticacion.dto.RegisterRequest;
import com.gym.autenticacion.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del AuthController usando Mockito.
 * Se simula AuthService para probar el controller de forma aislada,
 * sin levantar el contexto de Spring ni la base de datos.
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("juan.perez@gmail.com", "Clave123");
        registerRequest = new RegisterRequest(
                "12345678-9", "nuevo@gmail.com", "Clave123",
                "Juan", "Andres", "Perez", "Soto"
        );
    }

    // ───────────────────────── login ─────────────────────────

    @Test
    void login_credencialesValidas_retorna200ConToken() {
        LoginResponse respuestaEsperada = new LoginResponse("token.jwt.simulado");
        when(authService.login(loginRequest)).thenReturn(respuestaEsperada);

        ResponseEntity<LoginResponse> response = authController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("token.jwt.simulado", response.getBody().token());
        verify(authService).login(loginRequest);
    }

    @Test
    void login_usuarioNoExiste_lanzaUsernameNotFoundException() {
        when(authService.login(loginRequest))
                .thenThrow(new UsernameNotFoundException("Usuario no encontrado"));

        assertThrows(UsernameNotFoundException.class,
                () -> authController.login(loginRequest));
    }

    @Test
    void login_claveIncorrecta_lanzaBadCredentialsException() {
        when(authService.login(loginRequest))
                .thenThrow(new BadCredentialsException("Credenciales inválidas"));

        assertThrows(BadCredentialsException.class,
                () -> authController.login(loginRequest));
    }

    // ───────────────────────── register ─────────────────────────

    @Test
    void register_datosValidos_retorna200ConMensajeExito() {
        doNothing().when(authService).register(registerRequest);

        ResponseEntity<String> response = authController.register(registerRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Registro exitoso", response.getBody());
        verify(authService).register(registerRequest);
    }

    @Test
    void register_correoYaRegistrado_lanzaIllegalArgumentException() {
        doThrow(new IllegalArgumentException("Correo ya registrado"))
                .when(authService).register(registerRequest);

        assertThrows(IllegalArgumentException.class,
                () -> authController.register(registerRequest));
    }
}
