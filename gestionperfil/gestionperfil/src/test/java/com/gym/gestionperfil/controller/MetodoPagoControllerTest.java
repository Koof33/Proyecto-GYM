package com.gym.gestionperfil.controller;

import com.gym.gestionperfil.model.MetodoPago;
import com.gym.gestionperfil.security.RoleValidator;
import com.gym.gestionperfil.service.MetodoPagoService;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de MetodoPagoController usando Mockito.
 */
@ExtendWith(MockitoExtension.class)
class MetodoPagoControllerTest {

    @Mock
    private MetodoPagoService metodoPagoService;

    @Mock
    private RoleValidator validator;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private MetodoPagoController metodoPagoController;

    private MetodoPago metodoPago;

    @BeforeEach
    void setUp() {
        metodoPago = new MetodoPago(1L, "TARJETA", "Visa *1234", 2L);
    }

    // ───────────────────────── listar ─────────────────────────

    @Test
    void listar_devuelveMetodosDelUsuarioAutenticado() {
        when(validator.getUserId(request)).thenReturn(2L);
        when(metodoPagoService.listarPorUsuario(2L)).thenReturn(List.of(metodoPago));

        ResponseEntity<List<MetodoPago>> response = metodoPagoController.listar(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(validator).requireRole(request, "CLIENTE");
    }

    // ───────────────────────── agregar ─────────────────────────

    @Test
    void agregar_metodoValido_retorna200ConMetodoGuardado() {
        when(validator.getUserId(request)).thenReturn(2L);
        when(metodoPagoService.agregar(2L, metodoPago)).thenReturn(metodoPago);

        ResponseEntity<MetodoPago> response = metodoPagoController.agregar(metodoPago, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("TARJETA", response.getBody().getTipo());
    }

    // ───────────────────────── actualizar ─────────────────────────

    @Test
    void actualizar_metodoExistenteYPropio_retorna200() {
        when(validator.getUserId(request)).thenReturn(2L);
        when(metodoPagoService.actualizar(2L, 1L, metodoPago)).thenReturn(Optional.of(metodoPago));

        ResponseEntity<MetodoPago> response = metodoPagoController.actualizar(1L, metodoPago, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void actualizar_metodoInexistenteOAjeno_retorna404() {
        when(validator.getUserId(request)).thenReturn(2L);
        when(metodoPagoService.actualizar(2L, 99L, metodoPago)).thenReturn(Optional.empty());

        ResponseEntity<MetodoPago> response = metodoPagoController.actualizar(99L, metodoPago, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ───────────────────────── eliminar ─────────────────────────

    @Test
    void eliminar_metodoExistenteYPropio_retorna204() {
        when(validator.getUserId(request)).thenReturn(2L);
        when(metodoPagoService.eliminar(2L, 1L)).thenReturn(true);

        ResponseEntity<Void> response = metodoPagoController.eliminar(1L, request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void eliminar_metodoInexistenteOAjeno_retorna404() {
        when(validator.getUserId(request)).thenReturn(2L);
        when(metodoPagoService.eliminar(2L, 99L)).thenReturn(false);

        ResponseEntity<Void> response = metodoPagoController.eliminar(99L, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
